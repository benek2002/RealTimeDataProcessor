package com.example.SensorDataBackendProject.dto;

import com.example.SensorDataBackendProject.utilities.AlarmType;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long alarmId;
    private LocalDateTime localDateTime;
    private AlarmType alarmType;
    private double value;
}
