package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.SensorDataPM;
import com.example.SensorDataBackendProject.model.PredictionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.SensorDataBackendProject.model.SensorDataPoint;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat32;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PMPredictionService {

    private final SensorDataService sensorDataService;
    public PredictionRequest getSensorPoints(int historySize, int futureSize) {
        List<SensorDataPM> historicalData = sensorDataService.getDataFromRangeForPM(historySize);
        List<SensorDataPoint> sensorDataPoints = mapPMToSensorDataPoints(historicalData);
        return new PredictionRequest(sensorDataPoints, "pm2.5", futureSize, historySize);

    }

    private List<SensorDataPoint> mapPMToSensorDataPoints(List<SensorDataPM> historicalData){
        return historicalData.stream().map(data -> new SensorDataPoint(data.getTimestamp().toString(), data.getDustValue())).collect(Collectors.toList());
    }
}
