package com.example.SensorDataBackendProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity(name = "sensor_data_sw420")
@NoArgsConstructor
@Data
public class SensorDataSW420 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime timestamp;

    public Double occurred;

    public SensorDataSW420(LocalDateTime timestamp, Double occurred) {
        this.timestamp = timestamp;
        this.occurred = occurred;
    }
}
