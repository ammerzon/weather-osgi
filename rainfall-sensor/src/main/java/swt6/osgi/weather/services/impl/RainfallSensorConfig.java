package swt6.osgi.weather.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Rainfall sensor configuration",
        description = "The configuration for the rainfall sensor.")
public @interface RainfallSensorConfig {
    @AttributeDefinition(name = "Maximum rainfall per minute", description = "This specifies the maximum rainfall per minute.")
    double max_rainfall_minute() default 2.4;

    @AttributeDefinition(name = "Refresh interval", description = "This specifies the refresh interval of the sensor in milliseconds.")
    int interval() default 500;

    @AttributeDefinition(name = "Update rate", description = "The update rate in seconds.")
    long update_rate() default 900;
}
