package com.example.SensorDataBackendProject.controller;
import com.example.SensorDataBackendProject.entity.Image;
import com.example.SensorDataBackendProject.repository.ImageRepository;
import com.example.SensorDataBackendProject.service.ImageAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {

    private final ImageAlarmService imageAlarmService;
    @Value("${image.save.directory}")
    private String IMAGE_DIRECTORY;

    @PostMapping("/uploadImage")
    public String handleImageUpload(@RequestParam("file") MultipartFile file, @RequestParam("alarmOccurrenceId") long alarmOccurrenceId) {
        return imageAlarmService.saveImage(file, alarmOccurrenceId);
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(IMAGE_DIRECTORY).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Możesz zmienić typ MIME, jeśli potrzebne
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
