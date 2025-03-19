package com.example.SensorDataBackendProject.controller;

import com.example.SensorDataBackendProject.model.SensorDataPoint;
import com.example.SensorDataBackendProject.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/predict")
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/{sensorType}")
    public List<SensorDataPoint> predict(@PathVariable String sensorType,
                                         @RequestParam int historySize,
                                         @RequestParam int futureSteps) {

        return predictionService.predict(sensorType, historySize, futureSteps);
    }

}
