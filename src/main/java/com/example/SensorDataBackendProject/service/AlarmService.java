package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.dto.AlarmDTO;
import com.example.SensorDataBackendProject.dto.AlarmOccurenceDTO;
import com.example.SensorDataBackendProject.dto.ImageDTO;
import com.example.SensorDataBackendProject.dto.NotificationDTO;
import com.example.SensorDataBackendProject.entity.Alarm;
import com.example.SensorDataBackendProject.entity.AlarmOccurrence;
import com.example.SensorDataBackendProject.repository.AlarmOccurrenceRepository;
import com.example.SensorDataBackendProject.repository.AlarmRepository;
import com.example.SensorDataBackendProject.utilities.AlarmType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);

    private final AlarmRepository alarmRepository;

    private final AlarmOccurrenceRepository alarmOccurrenceRepository;
    private Map<Long, LocalDateTime> lastNotifiedTimeMap = new HashMap<>();

    private final SimpMessagingTemplate messagingTemplate;

    private final SMSService smsService;

    private final EmailService emailService;

    private final ImageAlarmService imageAlarmService;



    public Alarm saveAlarm(Alarm alarm) {
        alarm.setDateOfAlarmSet(LocalDateTime.now());
        return alarmRepository.save(alarm);
    }

    public List<AlarmDTO> getAllAlarms() {
        return alarmRepository.findAllAlarmsAsDTO();
    }

    public AlarmDTO getAlarmById(Long id) {
        Alarm alarm =  alarmRepository.findById(id).orElseThrow();
        return mapAlarmToAlarmDTO(alarm);
    }

    private AlarmDTO mapAlarmToAlarmDTO(Alarm alarm){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setId(alarm.getId());
        alarmDTO.setAlarmType(alarm.getAlarmType());
        alarmDTO.setValue(alarm.getValue());
        alarmDTO.setDateOfAlarmSet(alarm.getDateOfAlarmSet());
        alarmDTO.setEmail(alarm.getEmail());
        alarmDTO.setPhoneNumber(alarm.getPhoneNumber());
        alarmDTO.setTakePhoto(alarm.getTakePhoto());
        List<AlarmOccurenceDTO> alarmOccurenceDTOList = alarm.getOccurrences().stream().map(occurrence -> {
            String filePath = occurrence.getImage() != null ? occurrence.getImage().getFilePath() : null;


            String fileName = filePath != null ? filePath.substring(filePath.lastIndexOf("\\") + 1) : null;

            return new AlarmOccurenceDTO(
                    occurrence.getId(),
                    occurrence.getLocalDateTime(),
                    fileName != null ? new ImageDTO(occurrence.getImage().getId(), fileName) : null
            );
        }).collect(Collectors.toList());

        alarmDTO.setOccurrences(alarmOccurenceDTOList);
        return alarmDTO;
    }
    @Async
    public void checkForAlarmForDHT22(double temperature, double humidity)  {
        List<Alarm> allAlarms = alarmRepository.findAll();
        Optional<List<Alarm>> optionalTemperatureAlarms = checkForValue(temperature, AlarmType.TEMPERATURE,  allAlarms);
        Optional<List<Alarm>> optionalHumidityAlarms = checkForValue(humidity, AlarmType.HUMIDITY, allAlarms);

        if (optionalTemperatureAlarms.isPresent()) {
            optionalTemperatureAlarms.get().forEach(this::assignAlarmOccurrence);
        }
        if (optionalHumidityAlarms.isPresent()) {
            optionalHumidityAlarms.get().forEach(this::assignAlarmOccurrence);
        }
    }

    @Async
    public void checkForAlarmForMQ135(double CO)  {

        List<Alarm> allAlarms = alarmRepository.findAll();
        Optional<List<Alarm>> optionalCO2Alarms = checkForValue(CO, AlarmType.CO, allAlarms);

        if (optionalCO2Alarms.isPresent()) {
            optionalCO2Alarms.get().forEach(this::assignAlarmOccurrence);
        }

    }

    @Async
    public void checkForAlarmForMQ2(double LPG)  {

        List<Alarm> allAlarms = alarmRepository.findAll();
        Optional<List<Alarm>> optionalLPGAlarms = checkForValue(LPG, AlarmType.LPG, allAlarms);
        if (optionalLPGAlarms.isPresent()) {
            optionalLPGAlarms.get().forEach(this::assignAlarmOccurrence);
        }

    }

    @Async
    public void checkForAlarmForMQSensor(double value, AlarmType alarmType)  {

        List<Alarm> allAlarms = alarmRepository.findAll();
        Optional<List<Alarm>> optionalAlarms = checkForValue(value, alarmType, allAlarms);
        if (optionalAlarms.isPresent()) {
            optionalAlarms.get().forEach(this::assignAlarmOccurrence);
        }

    }
    @Async
    public void checkForAlarmForSW420() {
        List<Alarm> allAlarms = alarmRepository.findAll();
        List<Alarm> vibrationAlarms = allAlarms.stream()
                .filter(alarm -> alarm.getAlarmType().equals(AlarmType.VIBRATION)).toList();
        if (!vibrationAlarms.isEmpty()) {
           vibrationAlarms.forEach(this::assignAlarmOccurrence);
        }
    }

    @Async
    public void checkForAlarmForPM(double dustValue)  {

        List<Alarm> allAlarms = alarmRepository.findAll();
        Optional<List<Alarm>> optionalPMAlarms = checkForValue(dustValue, AlarmType.PM, allAlarms);

        if (optionalPMAlarms.isPresent()) {
            optionalPMAlarms.get().forEach(this::assignAlarmOccurrence);
        }

    }


    private Optional<List<Alarm>> checkForValue(double value, AlarmType alarmType, List<Alarm> alarms){
        List<Alarm> typeAlarms = alarms.stream().filter(alarm -> alarm.getAlarmType().equals(alarmType)).toList();
        List<Alarm> filteredAlarms = typeAlarms.stream().filter(alarm -> alarm.getValue() < value).toList();
        if (filteredAlarms.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(filteredAlarms);
        }
    }

    public synchronized void assignAlarmOccurrence(Alarm alarm) {
        AlarmOccurrence alarmOccurrence = new AlarmOccurrence();
        alarmOccurrence.setAlarm(alarm);
        alarmOccurrence.setLocalDateTime(LocalDateTime.now());
        alarmOccurrence.setImage(null);
        AlarmOccurrence alarmOccurrenceFromDatabase = alarmOccurrenceRepository.saveAndFlush(alarmOccurrence);
        alarm.getOccurrences().add(alarmOccurrenceFromDatabase);
        if(alarm.getTakePhoto()){
            imageAlarmService.captureImageFromPi(alarmOccurrenceFromDatabase.getId());
        }
        logger.info("Alarm wystąpił: " + alarm + ", wystąpienie: " + alarmOccurrence);
        checkForAlarmOccurrence(alarmOccurrenceFromDatabase, alarm);
    }

    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }

    public void checkForAlarmOccurrence(AlarmOccurrence occurrence, Alarm alarm) {

        Long alarmId = occurrence.getAlarm().getId();
        LocalDateTime now = LocalDateTime.now();
        if (lastNotifiedTimeMap.get(alarmId) == null ||
                lastNotifiedTimeMap.get(alarmId).isBefore(now.minusMinutes(1))) {
            NotificationDTO notification = new NotificationDTO(occurrence.getAlarm().getId(), occurrence.getLocalDateTime(), alarm.getAlarmType(), alarm.getValue());
            sendNotification(notification);
            String alarmMessage = "Alarm ustawiony na " + switch (alarm.getAlarmType()) {
                case TEMPERATURE -> "temperaturę";
                case HUMIDITY -> "wilgotność";
                case LPG -> "Skroplony gaz płynny (LPG)";
                case CO -> "tlenek węgla (CO)";
                case PM -> "drobne cząstki stałe (PM2.5)";
                case VIBRATION -> "drgania";
                default -> "nieznany typ";
            } + " o wartości " + alarm.getValue() + " wystąpił o godzinie " + occurrence.getLocalDateTime();
            if(alarm.getPhoneNumber() != null && alarm.getSendSMS()){
                smsService.sendSMS(alarm.getPhoneNumber(), alarmMessage);
            }
            if(alarm.getEmail() != null && alarm.getSendEmail()){
                emailService.sendEmail("pbz2002@gmail.com", "Wystąpienie Alarmu", alarmMessage);
            }
            lastNotifiedTimeMap.put(alarmId, now);
        }
    }

    private void sendNotification(NotificationDTO notification) {

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        logger.info("Wyświetlam alarm");
    }


}
