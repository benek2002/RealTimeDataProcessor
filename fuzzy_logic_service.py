from flask import Flask, request, jsonify
import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl

# Tworzenie aplikacji Flask
app = Flask(__name__)

lpg = ctrl.Antecedent(np.arange(0, 2001, 1), 'LPG')
co = ctrl.Antecedent(np.arange(0, 201, 1), 'CO')
pm25 = ctrl.Antecedent(np.arange(0, 581, 1), 'PM2.5')

# Zmienna wyjściowa
air_quality = ctrl.Consequent(np.arange(0, 11, 1), 'AirQuality')

# Definiowanie zbiorów rozmytych dla LPG
lpg['low'] = fuzz.trapmf(lpg.universe, [0, 0, 200, 400])
lpg['medium'] = fuzz.trimf(lpg.universe, [300, 700, 1000])
lpg['high'] = fuzz.trapmf(lpg.universe, [800, 1500, 2000, 2000])

# Definiowanie zbiorów rozmytych dla CO
co['low'] = fuzz.trapmf(co.universe, [0, 0, 25, 50])
co['medium'] = fuzz.trimf(co.universe, [35, 100, 150])
co['high'] = fuzz.trapmf(co.universe, [100, 150, 200, 200])

# Definiowanie zbiorów rozmytych dla PM2.5
pm25['very_low'] = fuzz.trapmf(pm25.universe, [0, 0, 20, 50])
pm25['low'] = fuzz.trimf(pm25.universe, [30, 60, 130])
pm25['medium'] = fuzz.trimf(pm25.universe, [80, 160, 300])
pm25['high'] = fuzz.trapmf(pm25.universe, [250, 450, 580, 580])

# Definiowanie zbiorów rozmytych dla jakości powietrza
air_quality['good'] = fuzz.trapmf(air_quality.universe, [0, 0, 1, 2.5])
air_quality['moderate'] = fuzz.trimf(air_quality.universe, [2, 3.5, 5])
air_quality['bad'] = fuzz.trimf(air_quality.universe, [4.5, 6.5, 7.5])
air_quality['very_bad'] = fuzz.trapmf(air_quality.universe, [7, 8.5, 10, 10])

# Definiowanie reguł
rules = [
    # LPG = low, CO2 = low
    ctrl.Rule(lpg['low'] & co['low'] & pm25['very_low'], air_quality['good']),
    ctrl.Rule(lpg['low'] & co['low'] & pm25['low'], air_quality['good']),
    ctrl.Rule(lpg['low'] & co['low'] & pm25['medium'], air_quality['moderate']),
    ctrl.Rule(lpg['low'] & co['low'] & pm25['high'], air_quality['bad']),

    # LPG = low, CO2 = medium
    ctrl.Rule(lpg['low'] & co['medium'] & pm25['very_low'], air_quality['moderate']),
    ctrl.Rule(lpg['low'] & co['medium'] & pm25['low'], air_quality['moderate']),
    ctrl.Rule(lpg['low'] & co['medium'] & pm25['medium'], air_quality['bad']),
    ctrl.Rule(lpg['low'] & co['medium'] & pm25['high'], air_quality['very_bad']),

    # LPG = low, CO2 = high
    ctrl.Rule(lpg['low'] & co['high'] & pm25['very_low'], air_quality['bad']),
    ctrl.Rule(lpg['low'] & co['high'] & pm25['low'], air_quality['bad']),
    ctrl.Rule(lpg['low'] & co['high'] & pm25['medium'], air_quality['very_bad']),
    ctrl.Rule(lpg['low'] & co['high'] & pm25['high'], air_quality['very_bad']),

    # LPG = medium, CO2 = low
    ctrl.Rule(lpg['medium'] & co['low'] & pm25['very_low'], air_quality['moderate']),
    ctrl.Rule(lpg['medium'] & co['low'] & pm25['low'], air_quality['moderate']),
    ctrl.Rule(lpg['medium'] & co['low'] & pm25['medium'], air_quality['bad']),
    ctrl.Rule(lpg['medium'] & co['low'] & pm25['high'], air_quality['very_bad']),

    # LPG = medium, CO2 = medium
    ctrl.Rule(lpg['medium'] & co['medium'] & pm25['very_low'], air_quality['moderate']),
    ctrl.Rule(lpg['medium'] & co['medium'] & pm25['low'], air_quality['moderate']),
    ctrl.Rule(lpg['medium'] & co['medium'] & pm25['medium'], air_quality['bad']),
    ctrl.Rule(lpg['medium'] & co['medium'] & pm25['high'], air_quality['very_bad']),

    # LPG = medium, CO2 = high
    ctrl.Rule(lpg['medium'] & co['high'] & pm25['very_low'], air_quality['bad']),
    ctrl.Rule(lpg['medium'] & co['high'] & pm25['low'], air_quality['bad']),
    ctrl.Rule(lpg['medium'] & co['high'] & pm25['medium'], air_quality['very_bad']),
    ctrl.Rule(lpg['medium'] & co['high'] & pm25['high'], air_quality['very_bad']),

    # LPG = high, CO2 = low
    ctrl.Rule(lpg['high'] & co['low'] & pm25['very_low'], air_quality['bad']),
    ctrl.Rule(lpg['high'] & co['low'] & pm25['low'], air_quality['bad']),
    ctrl.Rule(lpg['high'] & co['low'] & pm25['medium'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['low'] & pm25['high'], air_quality['very_bad']),

    # LPG = high, CO2 = medium
    ctrl.Rule(lpg['high'] & co['medium'] & pm25['very_low'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['medium'] & pm25['low'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['medium'] & pm25['medium'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['medium'] & pm25['high'], air_quality['very_bad']),

    # LPG = high, CO2 = high
    ctrl.Rule(lpg['high'] & co['high'] & pm25['very_low'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['high'] & pm25['low'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['high'] & pm25['medium'], air_quality['very_bad']),
    ctrl.Rule(lpg['high'] & co['high'] & pm25['high'], air_quality['very_bad']),
]

# Tworzenie systemu sterowania
air_ctrl = ctrl.ControlSystem(rules)
air_simulation = ctrl.ControlSystemSimulation(air_ctrl)

def get_membership_category(result):
    air_quality_good = fuzz.interp_membership(
        air_quality.universe, air_quality['good'].mf, result)
    print(f"Membership in 'good': {air_quality_good}")

    air_quality_moderate = fuzz.interp_membership(
        air_quality.universe, air_quality['moderate'].mf, result)
    print(f"Membership in 'moderate': {air_quality_moderate}")

    air_quality_bad = fuzz.interp_membership(
        air_quality.universe, air_quality['bad'].mf, result)
    print(f"Membership in 'bad': {air_quality_bad}")

    air_quality_very_bad = fuzz.interp_membership(
        air_quality.universe, air_quality['very_bad'].mf, result)
    print(f"Membership in 'very_bad': {air_quality_very_bad}")

    memberships = {
        "good": air_quality_good,
        "moderate": air_quality_moderate,
        "bad": air_quality_bad,
        "very_bad": air_quality_very_bad
    }

    max_category = max(memberships, key=memberships.get)
    print(f"Highest membership category: {max_category}")

    return max_category
@app.route('/fuzzy', methods=['POST'])
def predict_air_quality():
    """Endpoint do przewidywania jakości powietrza."""
    try:
        data = request.get_json()
        lpg_value = data['LPG']
        co_value = data['CO']
        pm25_value = data['PM2.5']

        air_simulation.input['LPG'] = lpg_value
        air_simulation.input['CO'] = co_value
        air_simulation.input['PM2.5'] = pm25_value

        # Obliczanie wyników
        air_simulation.compute()
        result = air_simulation.output['AirQuality']
        category = get_membership_category(result)

        # Zwracanie wyników jako JSON
        return jsonify({
            "AirQuality": result,
            "Category": category
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 400

# Uruchamianie serwera Flask
if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5002)