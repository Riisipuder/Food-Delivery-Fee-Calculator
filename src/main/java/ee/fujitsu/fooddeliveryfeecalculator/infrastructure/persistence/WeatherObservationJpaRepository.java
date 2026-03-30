package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeatherObservationJpaRepository extends JpaRepository<WeatherObservationEntity, Long> {

    Optional<WeatherObservationEntity> findFirstByStationNameOrderByObservedAtDesc(String stationName);

    Optional<WeatherObservationEntity> findByStationNameAndObservedAt(String stationName, Instant observedAt);

    List<WeatherObservationEntity> findAllByStationNameOrderByObservedAtAsc(String stationName);
}
