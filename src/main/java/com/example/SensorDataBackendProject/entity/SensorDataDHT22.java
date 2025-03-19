package com.example.SensorDataBackendProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "sensor_data_dht22")
@Data
public class SensorDataDHT22{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timestamp;
    private double temperature;
    private double humidity;

    public SensorDataDHT22(LocalDateTime now, double temperature, double humidity) {
        this.timestamp = now;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public SensorDataDHT22() {

    }
}
