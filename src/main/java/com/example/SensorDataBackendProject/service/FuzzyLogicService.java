package com.example.SensorDataBackendProject.service;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class FuzzyLogicService {

    private static final Logger logger = LoggerFactory.getLogger(FuzzyLogicService.class);

    // Zmieniamy metodę na asynchroniczną i zwracamy odpowiedź serwera Flask
    public String sendToFuzzyLogicEndpoint(double lpg, double co, double pm25) {
        String result = null;
        try {
            String url = "http://127.0.0.1:5002/fuzzy";
            JsonObject payload = new JsonObject();
            payload.addProperty("LPG", lpg);
            payload.addProperty("CO", co);
            payload.addProperty("PM2.5", pm25);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Dane wysłane do serwera Flask. Odpowiedź: " + response.body());

            // Zwracamy odpowiedź serwera jako wynik
            result = response.body();
        } catch (Exception e) {
            logger.error("Błąd podczas wysyłania danych do serwera Flask: " + e.getMessage(), e);
        }
        return result;
    }
}
