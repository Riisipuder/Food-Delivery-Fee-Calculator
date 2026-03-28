package ee.fujitsu.fooddeliveryfeecalculator.domain.port;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeatherObservationRepository {

    WeatherObservation save(WeatherObservation weatherObservation);

    Optional<WeatherObservation> findLatestByStationName(String stationName);

    Optional<WeatherObservation> findByStationNameAndObservedAt(String stationName, Instant observedAt);

    List<WeatherObservation> findAllByStationNameOrderByObservedAtAsc(String stationName);
}
