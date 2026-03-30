package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.persistence;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class WeatherObservationPersistenceAdapter implements WeatherObservationRepository {

    private final WeatherObservationJpaRepository weatherObservationJpaRepository;

    public WeatherObservationPersistenceAdapter(WeatherObservationJpaRepository weatherObservationJpaRepository) {
        this.weatherObservationJpaRepository = weatherObservationJpaRepository;
    }

    @Override
    public WeatherObservation save(WeatherObservation weatherObservation) {
        WeatherObservationEntity entity = toEntity(weatherObservation);
        WeatherObservationEntity savedEntity = weatherObservationJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<WeatherObservation> findLatestByStationName(String stationName) {
        return weatherObservationJpaRepository.findFirstByStationNameOrderByObservedAtDesc(stationName)
            .map(this::toDomain);
    }

    @Override
    public Optional<WeatherObservation> findByStationNameAndObservedAt(String stationName, Instant observedAt) {
        return weatherObservationJpaRepository.findByStationNameAndObservedAt(stationName, observedAt)
            .map(this::toDomain);
    }

    @Override
    public List<WeatherObservation> findAllByStationNameOrderByObservedAtAsc(String stationName) {
        return weatherObservationJpaRepository.findAllByStationNameOrderByObservedAtAsc(stationName)
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private WeatherObservationEntity toEntity(WeatherObservation weatherObservation) {
        WeatherObservationEntity entity = new WeatherObservationEntity();
        entity.setId(weatherObservation.id());
        entity.setStationName(weatherObservation.stationName());
        entity.setWmoCode(weatherObservation.wmoCode());
        entity.setAirTemperature(weatherObservation.airTemperature());
        entity.setWindSpeed(weatherObservation.windSpeed());
        entity.setWeatherPhenomenon(weatherObservation.weatherPhenomenon());
        entity.setObservedAt(weatherObservation.observedAt());
        return entity;
    }

    private WeatherObservation toDomain(WeatherObservationEntity entity) {
        return new WeatherObservation(
            entity.getId(),
            entity.getStationName(),
            entity.getWmoCode(),
            entity.getAirTemperature(),
            entity.getWindSpeed(),
            entity.getWeatherPhenomenon(),
            entity.getObservedAt()
        );
    }
}
