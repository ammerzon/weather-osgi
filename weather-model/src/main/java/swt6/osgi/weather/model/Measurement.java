package swt6.osgi.weather.model;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Measurement {

    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;
    private double value;
    private Unit unit;

    public Measurement(double value, Unit unit) {
        this(LocalDateTime.now(), value, unit);
    }

    public Measurement(LocalDateTime timeStamp, double value, Unit unit) {
        this.timeStamp = timeStamp;
        this.value = value;
        this.unit = unit;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ",
                Measurement.class.getSimpleName() + "[", "]")
                .add("timeStamp=" + timeStamp)
                .add("value=" + value)
                .add("unit='" + unit + "'")
                .toString();
    }
}
