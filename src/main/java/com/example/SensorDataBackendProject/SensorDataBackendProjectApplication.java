package com.example.SensorDataBackendProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SensorDataBackendProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataBackendProjectApplication.class, args);
	}

}
