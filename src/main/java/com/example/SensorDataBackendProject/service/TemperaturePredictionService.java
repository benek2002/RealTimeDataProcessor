package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.SensorDataDHT22;
import com.example.SensorDataBackendProject.model.PredictionRequest;
import com.example.SensorDataBackendProject.model.SensorDataPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tensorflow.*;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat32;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemperaturePredictionService {

    private final SensorDataService sensorDataService;
    public PredictionRequest getSensorPoints(int historySize, int futureSize) {
        List<SensorDataDHT22> historicalData = sensorDataService.getDataFromRangeForDHT22(historySize);
        List<SensorDataPoint> sensorDataPoints = mapDHT22ToSensorDataPoints(historicalData);
        return new PredictionRequest(sensorDataPoints, "temperature", futureSize, historySize);

    }

    private List<SensorDataPoint> mapDHT22ToSensorDataPoints(List<SensorDataDHT22> historicalData){
        return historicalData.stream().map(data -> new SensorDataPoint(data.getTimestamp().toString(), data.getTemperature())).collect(Collectors.toList());
    }
}
