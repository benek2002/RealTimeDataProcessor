package com.example.SensorDataBackendProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionRequest {

    private List<SensorDataPoint> sensorDataPoints;

    private String type;

    private int futureSize;

    private int historicalSize;
}
