package com.example.SensorDataBackendProject.controller;

import com.example.SensorDataBackendProject.entity.*;
import com.example.SensorDataBackendProject.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor-data")
public class SensorDataController {


    private final SensorDataService sensorDataService;

    @GetMapping("/dht22")
    public List<SensorDataDHT22> getDataFromRangeForDHT22(@RequestParam(defaultValue = "100") int size) {
        List<SensorDataDHT22> data = sensorDataService.getDataFromRangeForDHT22(size);
        return data;
    }

    @GetMapping("/mq135")
    public List<SensorDataMQ135> getDataFromRangeForMQ135(@RequestParam(defaultValue = "100") int size) {
        List<SensorDataMQ135> data = sensorDataService.getDataFromRangeForMQ135(size);
        return data;
    }

    @GetMapping("/mq2")
    public List<SensorDataMQ2> getDataFromRangeForMQ2(@RequestParam(defaultValue = "100") int size) {
        List<SensorDataMQ2> data = sensorDataService.getDataFromRangeForMQ2(size);
        return data;
    }

    @GetMapping("/sw420")
    public List<SensorDataSW420> getDataFromRangeForSW420(@RequestParam(defaultValue = "100") int size) {
        List<SensorDataSW420> data = sensorDataService.getDataFromRangeForSW420(size);
        return data;
    }

    @GetMapping("/pm")
    public List<SensorDataPM> getDataFromRangeForPM(@RequestParam(defaultValue = "100") int size) {
        List<SensorDataPM> data = sensorDataService.getDataFromRangeForPM(size);
        return data;
    }






}
