package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.SensorDataSW420;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataSW420Repository extends JpaRepository<SensorDataSW420, Long> {

    @Query(value = "SELECT * FROM sensor_data_sw420 ORDER BY id desc LIMIT :size", nativeQuery = true)
    List<SensorDataSW420> findLatestSamplesWithLimit(int size);
}
