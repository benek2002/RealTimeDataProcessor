package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.entity.*;
import com.example.SensorDataBackendProject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataDHT22Repository sensorDataDHT22Repository;
    private final SensorDataMQ135Repository sensorDataMQ135Repository;
    private final SensorDataMQ2Repository sensorDataMQ2Repository;
    private final SensorDataSW420Repository sensorDataSW420Repository;
    private final SensorDataPMRepository sensorDataPMRepository;




    public List<SensorDataDHT22> getDataFromRangeForDHT22(int size) {
        List<SensorDataDHT22> sensorDataList = sensorDataDHT22Repository.findLatestSamplesWithLimit(size);
        Collections.reverse(sensorDataList);
        return sensorDataList;
    }

    public List<SensorDataMQ135> getDataFromRangeForMQ135(int size) {
        List<SensorDataMQ135> sensorDataList = sensorDataMQ135Repository.findLatestSamplesWithLimit(size);
        Collections.reverse(sensorDataList);
        return sensorDataList;
    }

    public List<SensorDataSW420> getDataFromRangeForSW420(int size) {
        List<SensorDataSW420> sensorDataList = sensorDataSW420Repository.findLatestSamplesWithLimit(size);
        Collections.reverse(sensorDataList);
        return sensorDataList;
    }

    public List<SensorDataMQ2> getDataFromRangeForMQ2(int size) {
        List<SensorDataMQ2> sensorDataList = sensorDataMQ2Repository.findLatestSamplesWithLimit(size);
        Collections.reverse(sensorDataList);
        return sensorDataList;
    }

    public List<SensorDataPM> getDataFromRangeForPM(int size) {
        List<SensorDataPM> sensorDataList = sensorDataPMRepository.findLatestSamplesWithLimit(size);
        Collections.reverse(sensorDataList);
        return sensorDataList;
    }
}
