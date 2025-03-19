package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByAlarmOccurrence_Id(Long alarmOccurrenceId);
}

