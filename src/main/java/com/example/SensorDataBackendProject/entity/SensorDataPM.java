package com.example.SensorDataBackendProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "sensor_data_pm")
@Data
@NoArgsConstructor
public class SensorDataPM{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private double dustValue;

    public SensorDataPM(LocalDateTime timestamp, double dustValue) {
        this.timestamp = timestamp;
        this.dustValue = dustValue;
    }
}
