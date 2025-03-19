package com.example.SensorDataBackendProject.controller;

import com.example.SensorDataBackendProject.dto.AlarmDTO;
import com.example.SensorDataBackendProject.entity.Alarm;
import com.example.SensorDataBackendProject.service.AlarmService;
import com.example.SensorDataBackendProject.service.EmailService;
import com.example.SensorDataBackendProject.service.ImageAlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {

    private final ImageAlarmService imageAlarmService;
    private final AlarmService alarmService;

    @PostMapping("/create")
    public Alarm createAlarm(@RequestBody Alarm alarm) {
        return alarmService.saveAlarm(alarm);
    }

    @GetMapping
    public List<AlarmDTO> getAlarms() {
        return alarmService.getAllAlarms();
    }

    @GetMapping("/{id}")
    public AlarmDTO getAlarmById(@PathVariable Long id) {
        return alarmService.getAlarmById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAlarm(@PathVariable Long id){
        alarmService.deleteAlarm(id);
    }


}
