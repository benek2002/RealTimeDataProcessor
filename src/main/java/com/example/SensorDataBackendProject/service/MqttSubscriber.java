package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.*;
import com.example.SensorDataBackendProject.model.SensorDataPoint;
import com.example.SensorDataBackendProject.repository.*;
import com.example.SensorDataBackendProject.utilities.AlarmType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.SensorDataBackendProject.service.ImageAlarmService.*;


@Service
@RequiredArgsConstructor
public class MqttSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);
//    private static final String BROKER_URL = "tcp://192.168.201.41:1883";
    private static final String DHT22_TOPIC = "dht22/temperature_humidity";
    private static final String MQ135_TOPIC = "mq135/co_level";
    private static final String MQ2_TOPIC = "mq2/lpg_level";
    private static final String SW420_TOPIC = "sw420/vibration_detection";
    private static final String PM_TOPIC = "gp2y1010/dust_level";
    private static final String SCRIPT_PATH = "/home/pi/read_value_from_sensors_and_sent.py";

    private volatile long lastMessageTimestamp = System.currentTimeMillis();
    private static final long MESSAGE_TIMEOUT = 90000;
    private final SensorDataDHT22Repository sensorDataDHT22Repository;
    private final SensorDataMQ135Repository sensorDataMQ135Repository;
    private final SensorDataMQ2Repository sensorDataMQ2Repository;
    private final SensorDataSW420Repository sensorDataSW420Repository;
    private final SensorDataPMRepository sensorDataPMRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final AlarmService alarmService;
    private final FuzzyLogicService fuzzyLogicService;

    private MqttClient client;
    private String scriptPid;
    private final Map<String, Double> sensorDataBuffer = new ConcurrentHashMap<>();


    @Value("${mqtt.broker.ip}")
    private String brokerIp;
    private static final String PROTOCOL = "tcp://";
    private static final int PORT = 1883;

    private volatile boolean monitoringThreadRunning = false;
    public String getBrokerUrl() {
        return PROTOCOL + brokerIp + ":" + PORT;
    }
    @PostConstruct
    public synchronized void init() {
        try {

            client = new MqttClient(getBrokerUrl(), MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setConnectionTimeout(30);
            options.setAutomaticReconnect(true);
            options.setKeepAliveInterval(60);

            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    logger.error("MQTT connection lost: " + cause.getMessage(), cause);
                    try {
                        reconnect();
                        client.subscribe(DHT22_TOPIC);
                        client.subscribe(MQ135_TOPIC);
                        client.subscribe(MQ2_TOPIC);
                        client.subscribe(PM_TOPIC);
                        client.subscribe(SW420_TOPIC);
                        logger.info("Resubscribed to topics after reconnection.");
                        if (System.currentTimeMillis() - lastMessageTimestamp > MESSAGE_TIMEOUT) {
                            try {
                                stopClient();
                                init();
                            } catch (Exception e) {
                                logger.error("Error while restarting MQTT client: " + e.getMessage(), e);
                            }
                        }
                    } catch (MqttException e) {
                        logger.error("Error during resubscription: " + e.getMessage(), e);
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    lastMessageTimestamp = System.currentTimeMillis();
                    try {
                        String payload = new String(message.getPayload());
                        logger.info("Odebrano wiadomość na temacie: " + topic + " -> " + payload);

                        JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();

                        if (DHT22_TOPIC.equals(topic)) {
                            double temperature = jsonObject.get("temperature").getAsDouble();
                            double humidity = jsonObject.get("humidity").getAsDouble();
                            logger.info("DHT22 - Temperatura: " + temperature + " C, Wilgotność: " + humidity + " %");
                            saveSensorDataDHT22(temperature, humidity);

                        } else if (MQ135_TOPIC.equals(topic)) {
                            double coLevel = jsonObject.get("CO_ppm").getAsDouble();
                            coLevel = Math.round(coLevel * 10.0) / 10.0;
                            sensorDataBuffer.put("CO", coLevel);
                            logger.info("MQ135 - CO: " + coLevel);
                            saveSensorDataMQ135(coLevel);
                        } else if (MQ2_TOPIC.equals(topic)) {
                            double lpglevel = jsonObject.get("LPG_ppm").getAsDouble();
                            lpglevel = Math.round(lpglevel * 10.0) / 10.0;
                            sensorDataBuffer.put("LPG", lpglevel);
                            logger.info("MQ2 - lpq: " + lpglevel);
                            saveSensorDataMQ2(lpglevel);
                        } else if (SW420_TOPIC.equals(topic)) {
                            double occurred = jsonObject.get("vibration").getAsDouble();
                            logger.info("SW420 - occurred: " + occurred);
                            saveSensorDataSW420(occurred);
                        } else if (PM_TOPIC.equals(topic)) {
                            double pmlevel = jsonObject.get("dust_value").getAsDouble();
                            pmlevel = Math.round(pmlevel * 10.0) / 10.0;
                            sensorDataBuffer.put("PM2.5", pmlevel);
                            logger.info("PM - occurred: " + pmlevel);
                            saveSensorDataPM(pmlevel);
                        }

                        if (sensorDataBuffer.containsKey("LPG") && sensorDataBuffer.containsKey("CO") && sensorDataBuffer.containsKey("PM2.5")) {
                            onFullDataReceived(sensorDataBuffer.get("LPG"), sensorDataBuffer.get("CO"), sensorDataBuffer.get("PM2.5"));
                            sensorDataBuffer.clear();
                        }
                    } catch (Exception e) {
                        logger.error("Błąd podczas przetwarzania wiadomości: " + e.getMessage(), e);
                    }
                }

                public void onFullDataReceived(double lpg, double co, double pm25) {
                    // Wywołanie metody z FuzzyLogicService i odebranie odpowiedzi
                    String result = fuzzyLogicService.sendToFuzzyLogicEndpoint(lpg, co, pm25);

                    // Zwrócenie odpowiedzi w zależności od potrzeb (możesz to zrobić np. poprzez MessagingTemplate lub logowanie)
                    if (result != null) {
                        // Jeśli odpowiedź jest poprawna, możemy zrobić coś z wynikiem
                        // Przykładowo, możemy przekazać wynik do klienta (front-end) poprzez WebSocket
                        messagingTemplate.convertAndSend("/topic/fuzzy_result", result); // Wysyłamy wynik do klienta
                    } else {
                        logger.error("Brak odpowiedzi z serwera Flask.");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });

            connectAndSubscribe();

            monitorConnection();

        } catch (MqttException e) {
            logger.error("Error initializing MQTT subscriber", e);
        }
    }


    public synchronized void monitorConnection() {

        if (monitoringThreadRunning) {
            logger.info("Monitoring thread already running. Skipping...");
            return;
        }

        monitoringThreadRunning = true;
        new Thread(() -> {
            while (true) {
                try {
                    logger.info("Monitoring MQTT connection...");
                    long currentTime = System.currentTimeMillis();
                    if (client != null && !client.isConnected()) {
                        logger.warn("MQTT client disconnected. Attempting to reconnect...");
                        client.connect();
                        client.subscribe(DHT22_TOPIC);
                        client.subscribe(MQ135_TOPIC);
                        client.subscribe(MQ2_TOPIC);
                        client.subscribe(PM_TOPIC);
                        client.subscribe(SW420_TOPIC);
                    }else if (currentTime - lastMessageTimestamp > MESSAGE_TIMEOUT) {
                        logger.warn("No messages received in the last " + MESSAGE_TIMEOUT / 1000 + " seconds. Restarting MQTT client...");
                        try {
                            stopClient();
                            init();
                            Thread.sleep(90000);
                        } catch (Exception e) {
                            logger.error("Error while restarting MQTT client: " + e.getMessage(), e);
                        }
                    }
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    logger.error("Connection monitoring interrupted", e);
                    Thread.currentThread().interrupt();
                } catch (MqttSecurityException e) {
                    throw new RuntimeException(e);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void stopClient() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                logger.info("Successfully disconnected MQTT client.");
            }
        } catch (MqttException e) {
            logger.error("Error while disconnecting MQTT client: " + e.getMessage(), e);
        }
    }


    private void connectAndSubscribe() {
        try {
            if (!client.isConnected()) {
                client.connect();
                client.subscribe(DHT22_TOPIC);
                client.subscribe(MQ135_TOPIC);
                client.subscribe(MQ2_TOPIC);
                client.subscribe(PM_TOPIC);
                client.subscribe(SW420_TOPIC);
                logger.info("Connected to MQTT broker and subscribed to topics: " + DHT22_TOPIC + ", " + MQ135_TOPIC + ", " + SW420_TOPIC
                        + ", " + MQ2_TOPIC + ", " + PM_TOPIC);
                try {
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(PI_USERNAME, brokerIp, PI_PORT);
                    session.setPassword(PI_PASSWORD);
                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    String command = String.format("python3 %s", SCRIPT_PATH);
                    session.connect();
                    ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                    channelExec.setCommand(command);
                    channelExec.connect();
                    channelExec.disconnect();
                    session.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Błąd podczas uruchamiania skryptu na Raspberry Pi.");
                }
            }
        } catch (MqttException e) {
            logger.error("Error connecting to MQTT broker", e);
        }
    }

    private void reconnect() {
        while (!client.isConnected()) {
            try {
                logger.info("Attempting to reconnect to MQTT broker...");
                client.reconnect();
                client.subscribe(DHT22_TOPIC);
                client.subscribe(MQ135_TOPIC);
                client.subscribe(MQ2_TOPIC);
                client.subscribe(PM_TOPIC);
                client.subscribe(SW420_TOPIC);
                logger.info("Reconnected and resubscribed to topics.");
            } catch (MqttException e) {
                logger.error("Reconnection failed: " + e.getMessage());
                try {
                    Thread.sleep(5000); // Odczekaj przed kolejną próbą
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Async
    protected void saveSensorDataDHT22(double temperature, double humidity) {
        // Wyślij temperaturę jako ciąg znaków
        messagingTemplate.convertAndSend("/topic/temperature", String.valueOf(temperature));

        // Wyślij wilgotność jako ciąg znaków
        messagingTemplate.convertAndSend("/topic/humidity", String.valueOf(humidity));

        // Zapisz dane w bazie
        SensorDataDHT22 data = new SensorDataDHT22(LocalDateTime.now(), temperature, humidity);
        sensorDataDHT22Repository.save(data);

        // Sprawdź alarmya tu w ja
        alarmService.checkForAlarmForDHT22(temperature, humidity);
    }

    @Async
    protected void saveSensorDataMQ135(double coLevel) {
        double roundedCoLevel = Double.parseDouble(String.format("%.1f", coLevel));
        SensorDataMQ135 data = new SensorDataMQ135(LocalDateTime.now(), roundedCoLevel);
        messagingTemplate.convertAndSend("/topic/mq135", data);
        sensorDataMQ135Repository.save(data);
        alarmService.checkForAlarmForMQ135(coLevel);
    }

    @Async
    protected void saveSensorDataMQ2(double lpgLevel) {
        double roundedLpgLevel = Double.parseDouble(String.format("%.1f", lpgLevel));
        SensorDataMQ2 data = new SensorDataMQ2(LocalDateTime.now(), roundedLpgLevel);
        messagingTemplate.convertAndSend("/topic/mq2", data);
        sensorDataMQ2Repository.save(data);
        alarmService.checkForAlarmForMQ2(lpgLevel);
    }

    @Async
    protected void saveSensorDataPM(double dustValue) {
        SensorDataPM data = new  SensorDataPM(LocalDateTime.now(), dustValue);
        messagingTemplate.convertAndSend("/topic/pm", data);
        sensorDataPMRepository.save(data);
        alarmService.checkForAlarmForPM(dustValue);
    }

    @Async
    protected void saveSensorDataSW420(Double occurred) {
        SensorDataSW420 data = new SensorDataSW420(LocalDateTime.now(), occurred);
        messagingTemplate.convertAndSend("/topic/sw420", data);
        sensorDataSW420Repository.save(data);
        if (occurred == 1) {
            alarmService.checkForAlarmForSW420();
        }
    }

    @Bean(name = "taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("MyTaskExecutor-");
        executor.initialize();
        return executor;
    }




}