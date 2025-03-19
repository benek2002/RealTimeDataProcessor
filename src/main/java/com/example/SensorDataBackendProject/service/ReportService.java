package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.SensorDataDHT22;
import com.example.SensorDataBackendProject.model.ReportData;
import com.example.SensorDataBackendProject.model.SensorDataPoint;
import com.example.SensorDataBackendProject.repository.SensorDataDHT22Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SensorDataDHT22Repository sensorDataDHT22Repository;

    public ReportData generateReport(LocalDateTime startDate, LocalDateTime endDate, String dataType) {

        List<SensorDataDHT22> data = sensorDataDHT22Repository.findByDateRange(startDate, endDate);
        List<SensorDataPoint> reportPoints = data.stream()
                .map(d -> new SensorDataPoint(d.getTimestamp().toString(),
                        dataType.equals("temperature") ? d.getTemperature() : d.getHumidity()))
                .collect(Collectors.toList());

        double average = reportPoints.stream().mapToDouble(SensorDataPoint::getValue).average().orElse(0);
        double min = reportPoints.stream().mapToDouble(SensorDataPoint::getValue).min().orElse(0);
        double max = reportPoints.stream().mapToDouble(SensorDataPoint::getValue).max().orElse(0);

        return new ReportData(average, min, max, reportPoints);
    }
}
