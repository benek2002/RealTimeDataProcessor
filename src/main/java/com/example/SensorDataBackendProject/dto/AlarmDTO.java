package com.example.SensorDataBackendProject.dto;

import com.example.SensorDataBackendProject.utilities.AlarmType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDTO {

    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private double value;

    private LocalDateTime DateOfAlarmSet;

    private String email;

    private String phoneNumber;

    private Boolean takePhoto;

    private List<AlarmOccurenceDTO> occurrences = new ArrayList<>();

    public AlarmDTO(Long id, AlarmType alarmType, double value, LocalDateTime dateOfAlarmSet, String email, String phoneNumber, Boolean takePhoto) {
        this.id = id;
        this.alarmType = alarmType;
        this.value = value;
        DateOfAlarmSet = dateOfAlarmSet;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.takePhoto = takePhoto;
    }
}
