package com.example.SensorDataBackendProject.dto;

import com.example.SensorDataBackendProject.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmOccurenceDTO {

    private Long id;

    private LocalDateTime localDateTime;

    private ImageDTO imageDTO;

}
