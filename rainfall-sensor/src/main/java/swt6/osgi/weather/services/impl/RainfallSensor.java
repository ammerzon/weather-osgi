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

@Component(service = Sensor.class, property = ("sensorType:String=rainfall"))
@Designate(ocd = RainfallSensorConfig.class)
public class RainfallSensor implements Sensor {
    private final List<SensorListener> listeners = new CopyOnWriteArrayList<>();
    private int interval = 500; // milliseconds
    private long updateRate = 450; // seconds
    private double maxRainfallMinute = 2.4;
    private double maxRainfall = maxRainfallMinute * (updateRate / 60.0);
    private LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);
    private Timer timer = new Timer();

    private Measurement getCurrentRainfall() {
        var value = Math.random() * maxRainfall;
        currentTime = currentTime.plus(updateRate, ChronoUnit.SECONDS);
        return new Measurement(currentTime, value, Unit.mm);
    }

    @Activate
    private void activate(RainfallSensorConfig config) {
        modified(config);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var measurement = getCurrentRainfall();
                listeners.forEach(l -> l.valueChanged(measurement));
            }
        }, 0, interval);
    }

    @Modified
    private void modified(RainfallSensorConfig config) {
        if (config != null) {
            maxRainfallMinute = config.max_rainfall_minute();
            maxRainfall = maxRainfallMinute * (updateRate / 60.0);
            interval = config.interval();
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
