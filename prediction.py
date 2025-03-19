import pandas as pd
import numpy as np
import tensorflow as tf
from flask import Flask, request, jsonify
from sklearn.preprocessing import MinMaxScaler

app = Flask(__name__)

# Załaduj modele dla różnych sensorów i okresów prognozy
models = {
    'temperature_30min': tf.keras.models.load_model("models/best_temperature_30min_model.keras"),
    'temperature_4h': tf.keras.models.load_model("models/best_temperature_4h_model.keras"),
    'humidity_30min': tf.keras.models.load_model("models/best_humidity_30min_model.keras"),
    'humidity_4h': tf.keras.models.load_model("models/best_humidity_4h_model.keras"),
    'pm2.5_30min': tf.keras.models.load_model("models/best_pm2.5_30min_model.keras"),
    'pm2.5_4h': tf.keras.models.load_model("models/best_pm2.5_4h_model.keras")
}


def prepare_input_data(sensor_type, historical_data, history_size):
    print("Raw historical data received:", historical_data)

    # Tworzenie DataFrame z danych wejściowych
    data = pd.DataFrame(historical_data)
    print("DataFrame created:", data)

    # Weryfikacja brakujących wartości
    if data.isnull().any().any():
        print("Missing values detected in DataFrame:", data)

    # Konwersja timestamp na daty
    data['timestamp'] = pd.to_datetime(data['timestamp'], errors='coerce')  # Wykrywanie niepoprawnych dat
    print("Timestamps after conversion:", data['timestamp'])

    # Weryfikacja kolumny wartości (sensor_type)
    if sensor_type not in data.columns:
        data = data.rename(columns={"value": sensor_type})  # Upewniamy się, że kolumna ma odpowiednią nazwę

    print("Values before scaling:", data[sensor_type])

    # Skalowanie danych
    scaler = MinMaxScaler(feature_range=(0, 1))

    # Jeżeli dane zawierają NaN, podajemy szczegóły:
    if data[sensor_type].isnull().any():
        print("NaN values in data column detected:", data[sensor_type])

    scaled_data = scaler.fit_transform(data[sensor_type].values.reshape(-1, 1))
    print("Scaled data:", scaled_data)

    # Wyodrębnianie ostatnich `history_size` wartości
    x_input = scaled_data[-history_size:]
    print("Prepared input for model (x_input):", x_input)

    x_input = x_input.reshape((1, x_input.shape[0], x_input.shape[1]))  # (1, history_size, 1)
    return x_input, scaler


# Endpoint do przewidywania
@app.route('/predict', methods=['POST'])
def predict():
    try:
        # Odbiór danych z żądania
        print("Received request:", request.json)
        data = request.json
        sensor_type = data['type']  # np. 'temperature'
        historical_data = data['sensorDataPoints']  # Lista z punktami danych historycznych
        history_size = data['historicalSize']  # Liczba punktów historycznych do wykorzystania
        future_steps = data['futureSize']  # Liczba kroków predykcji (np. 180 dla 30 minut)

        # Wybór modelu na podstawie typu sensora i liczby kroków
        model_key = f"{sensor_type}_{'30min' if future_steps == 180 else '4h'}"

        if model_key not in models:
            return jsonify({'error': 'Model not found for the specified sensor type and prediction steps'}), 400

        model = models[model_key]  # Wybór odpowiedniego modelu
        # Przygotowanie danych wejściowych
        x_input, scaler = prepare_input_data(sensor_type, historical_data, history_size)
        # Wykonanie predykcji
        y_pred = model.predict(x_input)

        # Odwrotne skalowanie wyników
        y_pred_actual = scaler.inverse_transform(y_pred.reshape(-1, 1)).flatten()
        y_pred_actual = np.round(y_pred_actual, 1)
        print("Prepared predictions:", y_pred_actual.tolist())
        # Zwrócenie tylko przewidywanych wartości
        return jsonify( y_pred_actual.tolist())

    except Exception as e:
        return jsonify({'error': str(e)}), 400


# Uruchomienie aplikacji
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
