# RealTimeDataProcessor
RealTimeDataProcessor is a remote monitoring system for industrial environmental parameters, developed as an engineering thesis project. The system is built around a Raspberry Pi microcomputer and a set of environmental sensors. It supports real-time data acquisition, processing, visualization, anomaly detection, and predictive analytics using artificial intelligence.

🎥 Video Demo
A complete walkthrough and demonstration of the system in action is available on YouTube. 

📺 Watch the demo here: [RealTimeDataProcessor - System Presentation](https://www.youtube.com/watch?v=BQ4E6mFVPqs)

### Key Features

Real-time monitoring of environmental parameters (temperature, humidity, gas levels, particulate matter, vibrations)

Alert system with configurable thresholds

Automated image capture during alert events

Predictive analytics using LSTM neural networks

Fuzzy logic-based air quality assessment

Data visualization through a web dashboard

MQTT-based communication between system components

Modular microservices architecture (Java & Python)

### System architecture

The system follows a microservices client-server architecture:

Data acquisition: Sensors connected to a Raspberry Pi collect data.

Transmission: Sensor data is sent via MQTT to the backend server.

Backend: A Java Spring Boot application stores, analyzes, and serves the data.

AI & fuzzy logic: Separate Python microservices handle prediction (LSTM) and air quality assessment (fuzzy logic).

Frontend: A Thymeleaf-based web interface displays real-time and historical data.

Alert System: Triggers image capture and notifications (email/SMS) when thresholds are exceeded.

## Technologies used 

### Hardware

Raspberry Pi 4 Model B

Sensors: DHT22, MQ-2, MQ-135, GP2Y1010AU0F, SW-420

Camera Module: OdSeven Camera HD OV5647

### Software

The system is divided into several software components that communicate through network protocols and RESTful APIs. It follows a modular architecture based on the client-server model and microservices.

### Raspberry Pi (Edge Node)
Language: Python

Functionality:

Real-time data collection from sensors (temperature, humidity, gas levels, dust, vibrations)

Data packaging and transmission via MQTT

Triggering the camera to capture images during alerts

Key Libraries:

RPi.GPIO, gpiozero, adafruit_dht, paho-mqtt, picamera2, json

### Backend Server (Java + Spring Boot)
Language: Java

Frameworks: Spring Boot, Spring Data JPA, Hibernate

Functionality:

Receives and stores sensor data via MQTT and HTTP

Manages alarms and user configuration

Triggers Raspberry Pi image capture over SSH using JSch

Integrates with external AI and fuzzy logic microservices

Exposes REST APIs and serves web UI

Database: MySQL

### Web Interface
Technologies: Thymeleaf, HTML, CSS, JavaScript, Chart.js

Functionality:

Real-time visualization of sensor data and predictions

Configurable alarm management

Display of captured images and air quality evaluation

Communication: HTTP + WebSocket (SockJS + STOMP)

### AI Microservices (Python)
LSTM Prediction Service:

Frameworks: TensorFlow/Keras

Predicts future sensor readings (e.g. temperature, PM2.5)

Fuzzy Logic Service:

Library: scikit-fuzzy

Evaluates air quality using Mamdani fuzzy inference

Outputs human-readable air status (e.g. “Good”, “Moderate”, “Poor”)

### Notifications
Email: JavaMail API

SMS: Twilio SDK

Sent when configured alarms are triggered



