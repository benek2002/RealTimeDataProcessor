package com.example.SensorDataBackendProject.service;

import com.example.SensorDataBackendProject.model.PredictionRequest;
import com.example.SensorDataBackendProject.model.SensorDataPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final TemperaturePredictionService temperaturePredictionService;

    private final HumidityPredictionService humidityPredictionService;

    private final PMPredictionService pmPredictionService;

    private final RestTemplate restTemplate;

    private static final String PREDICTION_URL = "http://127.0.0.1:5000/predict"; // Endpoint Pythona
//    private static final String PREDICTION_URL = System.getenv("PREDICTION_URL"); // Endpoint Pythona

    public List<SensorDataPoint> predict(String sensorType, int historySize, int futureSteps) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("charset", "utf-8");

        // Stworzenie obiektu PredictionRequest dla odpowiedniego typu sensora
        PredictionRequest predictionRequest = getPredictionRequest(sensorType, historySize, futureSteps);

        // Wysłanie zapytania do serwera Pythona
        HttpEntity<PredictionRequest> requestEntity = new HttpEntity<>(predictionRequest, headers);
        // Wykonanie zapytania POST
            ResponseEntity<List<Double>> response = restTemplate.exchange(
                    PREDICTION_URL,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        if (response.getBody() == null) {
            throw new IllegalStateException("No predictions returned from Python service");
        }
        System.out.println("Response body: " + response.getBody());
        // Otrzymane przewidywane wartości (List<Double>)
        List<Double> predictedValues = response.getBody();

        return generatePredictedData(predictedValues, predictionRequest.getSensorDataPoints());
    }

    private PredictionRequest getPredictionRequest(String sensorType, int historySize, int futureSteps) {

        PredictionRequest predictionRequest;
        if (sensorType.equals("temperature")) {
            predictionRequest = temperaturePredictionService.getSensorPoints(historySize, futureSteps);
        } else if (sensorType.equals("humidity")) {
            predictionRequest = humidityPredictionService.getSensorPoints(historySize, futureSteps);
        } else if (sensorType.equals("pm2.5")) {
            predictionRequest = pmPredictionService.getSensorPoints(historySize, futureSteps);
        } else {
            throw new IllegalArgumentException("Unknown sensor type: " + sensorType);
        }
        return predictionRequest;

    }

    private List<SensorDataPoint> generatePredictedData(List<Double> predictions, List<SensorDataPoint> historicalData) {
        // Bierzemy ostatni punkt historyczny i pobieramy jego timestamp
        LocalDateTime lastTimestamp = LocalDateTime.parse(historicalData.get(historicalData.size() - 1).getTimestamp());

        // Przygotowanie listy wyników
        List<SensorDataPoint> predictedData = new ArrayList<>();

        // Formatowanie timestampu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // Dla każdej przewidywanej wartości dodajemy timestamp
        for (double prediction : predictions) {
            // Zwiększamy czas o 10 sekund
            lastTimestamp = lastTimestamp.plusSeconds(10);

            // Tworzymy nowy punkt danych z przewidywaną wartością i odpowiednim timestampem
            predictedData.add(new SensorDataPoint(lastTimestamp.format(formatter), prediction));
        }

        return predictedData;
    }


}
