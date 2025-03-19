package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.SensorDataMQ135;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataMQ135Repository extends JpaRepository<SensorDataMQ135, Long> {

    @Query(value = "SELECT * FROM sensor_data_mq135 ORDER BY id desc LIMIT :size", nativeQuery = true)
    List<SensorDataMQ135> findLatestSamplesWithLimit(int size);
}
