package com.example.SensorDataBackendProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDataPoint {

    private String timestamp;
    private double value;
}
