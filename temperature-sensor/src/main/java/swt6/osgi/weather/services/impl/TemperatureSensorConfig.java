package swt6.osgi.weather.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Temperature sensor configuration",
        description = "The configuration for the temperature sensor.")
public @interface TemperatureSensorConfig {
    @AttributeDefinition(name = "Minimum temperature", description = "This specifies the minimum temperature of the sensor.")
    int min_temperature() default -15;

    @AttributeDefinition(name = "Maximum temperature", description = "This specifies the maximum temperature of the sensor.")
    int max_temperature() default 35;

    @AttributeDefinition(name = "Refresh interval", description = "This specifies the refresh interval of the sensor in milliseconds.")
    int interval() default 500;

    @AttributeDefinition(name = "Temperature deviation", description = "The temperature deviation for the measurements.")
    double deviation() default 1.0;

    @AttributeDefinition(name = "Update rate", description = "The update rate in seconds.")
    long update_rate() default 60;

}