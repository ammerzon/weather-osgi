package swt6.osgi.weather.services;

import swt6.osgi.weather.model.Measurement;

public interface WeatherStation {
    boolean hasTemperatureSensor();

    Measurement getCurrentTemperature();

    boolean hasRainfallSensor();

    Measurement getCurrentRainfall();

    Measurement getCumulatedRainfall();

    boolean hasSolarSensors();

    Measurement getCurrentSolarRadiation();
}
