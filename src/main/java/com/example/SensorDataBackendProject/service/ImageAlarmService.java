package com.example.SensorDataBackendProject.service;
import com.example.SensorDataBackendProject.entity.AlarmOccurrence;
import com.example.SensorDataBackendProject.entity.Image;
import com.example.SensorDataBackendProject.repository.AlarmOccurrenceRepository;
import com.example.SensorDataBackendProject.repository.ImageRepository;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Service
@RequiredArgsConstructor
public class ImageAlarmService {

    private final ImageRepository imageRepository;

    private final AlarmOccurrenceRepository alarmOccurrenceRepository;
    public static final String PI_USERNAME = "pi";
//    public static final String PI_HOST = "192.168.201.41";
    public static final int PI_PORT = 22;
    public static final String PI_PASSWORD = "raspberry";
    private static final String SCRIPT_PATH = "/home/pi/capture_and_send_direct.py";
//    private static final String JAVA_SERVER_URL = "http://192.168.201.72:8080/api/uploadImage";

    private static final String IMAGE_SAVE_DIRECTORY = "uploads";

    @Value("${image.save.directory}")
    private String ImageSaveDirectory;
    @Value("${java.server.url}")
    private String javaServerUrl;
    @Value("${mqtt.broker.ip}")
    private String brokerIp;
    public String captureImageFromPi(long alarmOccurrenceId) {
        // Przekazujemy ID wystąpienia alarmu oraz adres endpointa do skryptu na Raspberry Pi
        String command = String.format("python3 %s %d %s", SCRIPT_PATH, alarmOccurrenceId, javaServerUrl);
        String result;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(PI_USERNAME, brokerIp, PI_PORT);
            session.setPassword(PI_PASSWORD);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);

            // Uruchamiamy skrypt, ale nie odbieramy żadnych danych przez InputStream
            channelExec.connect();

            // Sprawdzamy, czy skrypt zakończył się sukcesem
            int exitStatus = channelExec.getExitStatus();
            if (exitStatus == 0) {
                result = "Skrypt na Raspberry Pi zakończył się pomyślnie.";
            } else {
                result = "Skrypt na Raspberry Pi zakończył się z błędem.";
            }

            // Zamykamy kanał i sesję
            channelExec.disconnect();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Błąd podczas uruchamiania skryptu na Raspberry Pi.";
        }
        return result;
    }

    public String saveImage(MultipartFile file, long alarmOccurrenceId) {
        try {
            // Upewnienie się, że katalog zapisu istnieje
            File directory = new File(ImageSaveDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tworzenie nazwy pliku
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "image_" + alarmOccurrenceId + "_" + timestamp + ".jpg";
            File imageFile = Paths.get(ImageSaveDirectory, fileName).toFile();

            // Zapis danych obrazu do pliku
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(file.getBytes());
            }

            // Zapis encji Image
            AlarmOccurrence alarmOccurrence = alarmOccurrenceRepository.findById(alarmOccurrenceId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono AlarmOccurrence o ID: " + alarmOccurrenceId));
            Image image = new Image();
            image.setAlarmOccurrence(alarmOccurrence);
            image.setFilePath(imageFile.getAbsolutePath());
            imageRepository.save(image);

            return "Zdjęcie zapisane.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Błąd podczas zapisu zdjęcia.";
        }
    }



}
