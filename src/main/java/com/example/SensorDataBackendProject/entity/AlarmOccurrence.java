package com.example.SensorDataBackendProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@ToString(exclude = "alarm")
public class AlarmOccurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime localDateTime;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    @OneToOne(mappedBy = "alarmOccurrence", cascade = CascadeType.ALL)
    private Image image;

}
