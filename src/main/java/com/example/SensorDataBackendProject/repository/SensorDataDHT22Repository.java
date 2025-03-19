package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.SensorDataDHT22;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorDataDHT22Repository extends JpaRepository<SensorDataDHT22, Long> {

    List<SensorDataDHT22> findByTimestampAfter(LocalDateTime timestamp);
    @Query(value = "SELECT * FROM sensor_data_dht22 ORDER BY id desc LIMIT :size", nativeQuery = true)
    List<SensorDataDHT22> findLatestSamplesWithLimit(int size);

    @Query("SELECT s FROM sensor_data_dht22 s WHERE s.timestamp BETWEEN :startDate AND :endDate")
    List<SensorDataDHT22> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
