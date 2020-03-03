package swt6.osgi.weather.model;

public interface Sensor {
    void addSensorListener(SensorListener listener);

    void removeSensorListener(SensorListener listener);
}
