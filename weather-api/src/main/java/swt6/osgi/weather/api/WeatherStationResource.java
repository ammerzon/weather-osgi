package swt6.osgi.weather.api;

import org.osgi.service.component.annotations.*;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import swt6.osgi.weather.services.WeatherStation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component(service = WeatherStationResource.class)
@JaxrsApplicationSelect("(osgi.jaxrs.name=.default)")
@JaxrsResource
@Path("/weather")
public class WeatherStationResource {

    @Reference(
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY
    )
    private volatile WeatherStation station;

    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public String welcomeMessage() {
        return "Welcome To Weather OSGi";
    }

    @Path("/temperature")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getTemperature() {
        var station = this.station;
        if (station == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Weather station unavailable").build();
        }
        if (!station.hasTemperatureSensor()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No temperature sensor available").build();
        }
        if (station.getCurrentTemperature() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No temperature sensor data available").build();
        }
        return Response.ok(station.getCurrentTemperature()).build();
    }

    @Path("/rainfall")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getRainfall(@DefaultValue("false") @QueryParam("cumulated") boolean cumulated) {
        var station = this.station;
        if (station == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Weather station unavailable").build();
        }
        if (!station.hasRainfallSensor()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No rainfall sensor available").build();
        }
        if (station.getCurrentRainfall() == null && !cumulated || station.getCumulatedRainfall() == null && cumulated) {
            return Response.status(Response.Status.NOT_FOUND).entity("No rainfall sensor data available").build();
        }
        return Response.ok(cumulated ? station.getCumulatedRainfall() : station.getCurrentRainfall()).build();
    }

    @Path("/solar")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getSolarRadiation() {
        var station = this.station;
        if (station == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Weather station unavailable").build();
        }
        if (!station.hasSolarSensors()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No solar sensor available").build();
        }
        if (station.getCurrentSolarRadiation() == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No solar sensor data available").build();
        }
        return Response.ok(station.getCurrentSolarRadiation()).build();
    }
}
