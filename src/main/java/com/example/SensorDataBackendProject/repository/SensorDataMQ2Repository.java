package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.SensorDataMQ135;
import com.example.SensorDataBackendProject.entity.SensorDataMQ2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataMQ2Repository extends JpaRepository<SensorDataMQ2, Long> {

    @Query(value = "SELECT * FROM sensor_data_mq2 ORDER BY id desc LIMIT :size", nativeQuery = true)
    List<SensorDataMQ2> findLatestSamplesWithLimit(int size);
}
