package swt6.osgi.weather.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import swt6.osgi.weather.model.Measurement;
import swt6.osgi.weather.model.Sensor;
import swt6.osgi.weather.model.SensorListener;
import swt6.osgi.weather.model.Unit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(service = Sensor.class, property = {"sensorType:String=temperature", "service.ranking:Integer=100"})
public class TemperatureSensorAdvanced implements Sensor {

    private static final int MIN_TEMPERATURE = -15;
    private static final int MAX_TEMPERATURE = 35;
    private static final int INTERVAL = 500;     // milliseconds
    private static final double DEVIATION = 1.0;
    private static final long UPDATE_RATE_IN_SECONDS = 60;

    private final List<SensorListener> listeners = new CopyOnWriteArrayList<>();

    private double currentValue = 0;
    private LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);
    private boolean isInitialized = false;
    private Timer timer = new Timer();

    private Measurement getCurrentTemperature() {
        if (!isInitialized) {
            currentValue =
                    MIN_TEMPERATURE + (Math.random()) * (MAX_TEMPERATURE - MIN_TEMPERATURE);
            isInitialized = true;
        } else {
            int sign = 0;
            if (currentValue < MIN_TEMPERATURE)
                sign = 1;
            else if (currentValue > MAX_TEMPERATURE)
                sign = -1;
            else
                sign = Math.random() < 0.5 ? -1 : 1;
            currentValue += sign * Math.random() * DEVIATION;
            currentTime = currentTime.plus(UPDATE_RATE_IN_SECONDS, ChronoUnit.SECONDS);
        }

        return new Measurement(currentTime, currentValue, Unit.Celsius);
    }

    @Activate
    private void activate() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var measurement = getCurrentTemperature();
                listeners.forEach(l -> l.valueChanged(measurement));
            }
        }, 0, INTERVAL);
    }

    @Deactivate
    private void deactivate() {
        timer.cancel();
    }

    @Override
    public void addSensorListener(SensorListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeSensorListener(SensorListener listener) {
        this.listeners.remove(listener);
    }
}
