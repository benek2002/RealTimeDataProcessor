package com.example.SensorDataBackendProject.repository;

import com.example.SensorDataBackendProject.entity.AlarmOccurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmOccurrenceRepository extends JpaRepository<AlarmOccurrence, Long> {
}
