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

@Component(service = Sensor.class, property = ("sensorType:String=rainfall"))
public class RainfallSensor implements Sensor {
    private static final int INTERVAL = 500; // milliseconds
    private static final long UPDATE_RATE = 900; // seconds
    private static final double MAX_RAINFALL_MINUTE = 2.4;
    private static final double MAX_RAINFALL = MAX_RAINFALL_MINUTE * (UPDATE_RATE / 60.0);

    private final List<SensorListener> listeners = new CopyOnWriteArrayList<>();

    private LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);
    private Timer timer = new Timer();

    private Measurement getCurrentRainfall() {
        var value = Math.random() * MAX_RAINFALL;
        currentTime = currentTime.plus(UPDATE_RATE, ChronoUnit.SECONDS);
        return new Measurement(currentTime, value, Unit.mm);
    }

    @Activate
    private void activate() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var measurement = getCurrentRainfall();
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
