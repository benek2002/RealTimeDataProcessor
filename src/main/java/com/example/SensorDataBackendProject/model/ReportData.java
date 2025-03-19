package com.example.SensorDataBackendProject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportData {

    private double average;
    private double min;
    private double max;
    private List<SensorDataPoint> data;
}

