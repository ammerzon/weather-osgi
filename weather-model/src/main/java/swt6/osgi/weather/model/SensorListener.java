package swt6.osgi.weather.model;

@FunctionalInterface
public interface SensorListener {
    void valueChanged(Measurement measurement);
}
