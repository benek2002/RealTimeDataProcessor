package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.dto.AlarmDTO;
import com.example.SensorDataBackendProject.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("SELECT new com.example.SensorDataBackendProject.dto.AlarmDTO(a.id, a.alarmType, a.value, a.DateOfAlarmSet, a.email, a.phoneNumber, a.takePhoto) " +
                  "FROM alarms a")
    List<AlarmDTO> findAllAlarmsAsDTO();

}
