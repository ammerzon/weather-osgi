package swt6.osgi.weather.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
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

@Component(service = Sensor.class, property = ("sensorType:String=temperature"))
@Designate(ocd = TemperatureSensorConfig.class)
public class TemperatureSensor implements Sensor {

    private final List<SensorListener> listeners = new CopyOnWriteArrayList<>();
    private int minTemperature = -15;
    private int maxTemperature = 35;
    private int interval = 500;     // milliseconds
    private double deviation = 1.0;
    private long updateRate = 60; // seconds
    private double currentValue = 0;
    private LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);
    private boolean isInitialized = false;
    private Timer timer = new Timer();

    private Measurement getCurrentTemperature() {
        if (!isInitialized) {
            currentValue =
                    minTemperature + (Math.random()) * (maxTemperature - minTemperature);
            isInitialized = true;
        } else {
            int sign = 0;
            if (currentValue < minTemperature)
                sign = 1;
            else if (currentValue > maxTemperature)
                sign = -1;
            else
                sign = Math.random() < 0.5 ? -1 : 1;
            currentValue += sign * Math.random() * deviation;
            currentTime = currentTime.plus(updateRate, ChronoUnit.SECONDS);
        }

        return new Measurement(currentTime, currentValue, Unit.Celsius);
    }

    @Activate
    private void activate(TemperatureSensorConfig config) {
        currentTime = LocalDateTime.now();
        modified(config);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var measurement = getCurrentTemperature();
                listeners.forEach(l -> l.valueChanged(measurement));
            }
        }, 0, interval);
    }

    @Modified
    private void modified(TemperatureSensorConfig config) {
        if (config != null) {
            minTemperature = config.min_temperature();
            maxTemperature = config.max_temperature();
            interval = config.interval();
            deviation = config.deviation();
            updateRate = config.update_rate();
        }
    }

    @Deactivate
    private void deactivate() {
        currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);;
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
