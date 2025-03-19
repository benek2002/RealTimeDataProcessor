package com.example.SensorDataBackendProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "sensor_data_mq135")
@Data
public class SensorDataMQ135 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private double coPpm;

    public SensorDataMQ135(LocalDateTime now,double  coPpm) {
        this.coPpm = coPpm;
        this.timestamp = now;
    }

    public SensorDataMQ135() {

    }




}
