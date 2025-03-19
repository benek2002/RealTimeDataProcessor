package com.example.SensorDataBackendProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
@Entity(name = "sensor_data_mq2")
@Data
public class SensorDataMQ2{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private double lpgPpm;

    public SensorDataMQ2(LocalDateTime now,double  lpgPpm) {
        this.lpgPpm = lpgPpm;
        this.timestamp = now;

    }
    public SensorDataMQ2() {

    }
}
