package ee.fujitsu.fooddeliveryfeecalculator.domain.port;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;

import java.util.List;

public interface WeatherObservationFeedClient {

    List<WeatherFeedObservation> fetchSupportedObservations();
}
