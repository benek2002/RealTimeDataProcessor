package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.SensorDataMQ135;
import com.example.SensorDataBackendProject.entity.SensorDataPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataPMRepository extends JpaRepository<SensorDataPM, Long> {

    @Query(value = "SELECT * FROM sensor_data_pm ORDER BY id desc LIMIT :size", nativeQuery = true)
    List<SensorDataPM> findLatestSamplesWithLimit(int size);
}
