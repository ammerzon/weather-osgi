package swt6.osgi.weather.services.impl;

import org.osgi.service.component.annotations.*;
import swt6.osgi.weather.model.Measurement;
import swt6.osgi.weather.model.Sensor;
import swt6.osgi.weather.services.WeatherStation;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(service = WeatherStation.class)
public class WeatherStationImpl implements WeatherStation {

    private static final long CUMULATED_RAINFALL_INTERVAL = 1; // hours

    private volatile Sensor temperatureSensor;
    private volatile Sensor rainfallSensor;
    private volatile List<Sensor> solarSensors = new CopyOnWriteArrayList<>();
    private volatile Measurement cumulatedRainfall;
    private volatile Measurement currentCumulatedRainfall;
    private volatile Measurement currentRainfall;
    private volatile Measurement currentTemperature;
    private volatile List<Measurement> solarRadiationMeasurements = new CopyOnWriteArrayList<>();
    private volatile Measurement currentSolarRadiation;

    //region Temperature sensor
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY,
            target = "(sensorType=temperature)"
    )
    public void setTemperatureSensor(Sensor temperatureSensor) {
        this.temperatureSensor = temperatureSensor;
        this.temperatureSensor.addSensorListener(this::processTemperatureMeasurement);
        System.out.println("Set " + temperatureSensor.getClass());
    }

    public void unsetTemperatureSensor(Sensor temperatureSensor) {
        this.temperatureSensor.removeSensorListener(this::processTemperatureMeasurement);
        if (this.temperatureSensor == temperatureSensor) {
            this.temperatureSensor = null;
        }
        System.out.println("Unset " + temperatureSensor.getClass());
    }

    private void processTemperatureMeasurement(Measurement measurement) {
        currentTemperature = measurement;
    }

    @Override
    public boolean hasTemperatureSensor() {
        return this.temperatureSensor != null;
    }

    @Override
    public Measurement getCurrentTemperature() {
        if (!hasTemperatureSensor()) {
            throw new IllegalStateException("No temperature sensor connected.");
        }
        return currentTemperature;
    }
    //endregion

    //region Rainfall sensor
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY,
            target = "(sensorType=rainfall)"
    )
    public void setRainfallSensor(Sensor rainfallSensor) {
        this.rainfallSensor = rainfallSensor;
        this.rainfallSensor.addSensorListener(this::processRainfallMeasurement);
        System.out.println("Set " + rainfallSensor.getClass());
    }

    public void unsetRainfallSensor(Sensor rainfallSensor) {
        this.rainfallSensor.removeSensorListener(this::processRainfallMeasurement);
        if (this.rainfallSensor == rainfallSensor) {
            this.rainfallSensor = null;
        }
        System.out.println("Unset " + rainfallSensor.getClass());
    }

    private void processRainfallMeasurement(Measurement measurement) {
        currentRainfall = measurement;
        if (currentCumulatedRainfall == null) {
            currentCumulatedRainfall = new Measurement(measurement.getValue(), measurement.getUnit());
        } else {
            currentCumulatedRainfall.setValue(currentCumulatedRainfall.getValue() + measurement.getValue());
            if (ChronoUnit.HOURS.between(measurement.getTimeStamp(), currentCumulatedRainfall.getTimeStamp()) > CUMULATED_RAINFALL_INTERVAL) {
                currentCumulatedRainfall.setTimeStamp(measurement.getTimeStamp());
                cumulatedRainfall = currentCumulatedRainfall;
                currentCumulatedRainfall = null;
            }
        }
    }

    @Override
    public boolean hasRainfallSensor() {
        return this.rainfallSensor != null;

    }

    @Override
    public Measurement getCurrentRainfall() {
        if (!hasRainfallSensor()) {
            throw new IllegalStateException("No rainfall sensor connected.");
        }
        return currentRainfall;
    }

    @Override
    public Measurement getCumulatedRainfall() {
        return cumulatedRainfall;
    }
    //endregion

    //region Solar radiation sensor
    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY,
            target = "(sensorType=solar)"
    )
    public void setSolarSensor(Sensor solarSensor) {
        solarSensors.add(solarSensor);
        solarSensor.addSensorListener(this::processSolarRadiationMeasurement);
        System.out.printf("Add %s [%d]%n", solarSensor.getClass(), solarSensors.size());
    }

    public void unsetSolarSensor(Sensor solarSensor) {
        var sensor = solarSensors.get(solarSensors.indexOf(solarSensor));
        sensor.removeSensorListener(this::processSolarRadiationMeasurement);
        solarSensors.remove(solarSensor);
        System.out.printf("Remove %s [%d]%n", solarSensor.getClass(), solarSensors.size());
    }

    private void processSolarRadiationMeasurement(Measurement measurement) {
        if (solarRadiationMeasurements.size() > 0 && solarRadiationMeasurements.get(0).getTimeStamp().isBefore(measurement.getTimeStamp())) {
            var avgSolarRadiation = solarRadiationMeasurements.stream().mapToDouble(Measurement::getValue).average().orElse(0);
            currentSolarRadiation = solarRadiationMeasurements.get(0);
            currentSolarRadiation.setValue(avgSolarRadiation);
            solarRadiationMeasurements.clear();
            return;
        }

        solarRadiationMeasurements.add(measurement);
    }

    @Override
    public boolean hasSolarSensors() {
        return this.solarSensors.size() > 0;

    }

    @Override
    public Measurement getCurrentSolarRadiation() {
        if (!hasSolarSensors()) {
            throw new IllegalStateException("No solar sensor connected.");
        }
        return currentSolarRadiation;
    }
    //endregion

    @Activate
    protected void activated() {
        System.out.println("Station activated");
    }
}
