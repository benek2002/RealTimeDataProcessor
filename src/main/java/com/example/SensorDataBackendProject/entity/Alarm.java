package com.example.SensorDataBackendProject.entity;

import com.example.SensorDataBackendProject.utilities.AlarmType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "alarms")
@Data
@ToString(exclude = "occurrences")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private float value;

    private LocalDateTime DateOfAlarmSet;

    private String email;

    private String phoneNumber;

    private Boolean takePhoto;

    private Boolean sendEmail;

    private Boolean sendSMS;

    @OneToMany(mappedBy = "alarm", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<AlarmOccurrence> occurrences;
}
