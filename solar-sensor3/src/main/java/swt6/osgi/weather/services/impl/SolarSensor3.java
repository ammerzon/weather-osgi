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

@Component(service = Sensor.class, property = ("sensorType:String=solar"))
@Designate(ocd = SolarSensor3Config.class)
public class SolarSensor3 implements Sensor {
    private final List<SensorListener> listeners = new CopyOnWriteArrayList<>();
    private int interval = 500; // milliseconds
    private long updateRate = 60; // seconds
    private double minSolarRadiation = 100;
    private double maxSolarRadiation = 900;
    private LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0);
    private Timer timer = new Timer();

    private Measurement getCurrentSolarRadiation() {
        var currentValue = minSolarRadiation + (Math.random()) * (maxSolarRadiation - minSolarRadiation);
        currentTime = currentTime.plus(updateRate, ChronoUnit.SECONDS);
        return new Measurement(currentTime, currentValue, Unit.wPm2);
    }

    @Activate
    private void activate(SolarSensor3Config config) {
        currentTime = LocalDateTime.now();
        modified(config);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var measurement = getCurrentSolarRadiation();
                listeners.forEach(l -> l.valueChanged(measurement));
            }
        }, 0, interval);
    }

    @Modified
    private void modified(SolarSensor3Config config) {
        if (config != null) {
            minSolarRadiation = config.min_solar_radiation();
            maxSolarRadiation = config.max_solar_radiation();
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
