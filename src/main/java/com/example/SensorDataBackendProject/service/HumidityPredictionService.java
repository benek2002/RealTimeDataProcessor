package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.SensorDataDHT22;
import com.example.SensorDataBackendProject.model.PredictionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.SensorDataBackendProject.model.SensorDataPoint;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HumidityPredictionService {

    private final SensorDataService sensorDataService;
    public PredictionRequest getSensorPoints(int historySize, int futureSize) {
        List<SensorDataDHT22> historicalData = sensorDataService.getDataFromRangeForDHT22(historySize);
        List<SensorDataPoint> sensorDataPoints = mapDHT22ToSensorDataPoints(historicalData);
        return new PredictionRequest(sensorDataPoints, "humidity", futureSize, historySize);

    }

    private List<SensorDataPoint> mapDHT22ToSensorDataPoints(List<SensorDataDHT22> historicalData){
        return historicalData.stream().map(data -> new SensorDataPoint(data.getTimestamp().toString(), data.getHumidity())).collect(Collectors.toList());
    }
}
