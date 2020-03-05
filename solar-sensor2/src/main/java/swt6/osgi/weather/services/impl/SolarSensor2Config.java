package swt6.osgi.weather.services.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Solar sensor (v2) configuration",
        description = "The configuration for the solar sensor (v2).")
public @interface SolarSensor2Config {
    @AttributeDefinition(name = "Minimum solar radiation", description = "This specifies the minimum solar radiation of the sensor.")
    int min_solar_radiation() default 20;

    @AttributeDefinition(name = "Maximum solar radiation", description = "This specifies the maximum solar radiation of the sensor.")
    int max_solar_radiation() default 1100;

    @AttributeDefinition(name = "Refresh interval", description = "This specifies the refresh interval of the sensor in milliseconds.")
    int interval() default 500;

    @AttributeDefinition(name = "Update rate", description = "The update rate in seconds.")
    long update_rate() default 60;
}
