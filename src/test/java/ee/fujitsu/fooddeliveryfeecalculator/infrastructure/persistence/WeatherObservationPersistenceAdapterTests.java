package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.persistence;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(WeatherObservationPersistenceAdapter.class)
class WeatherObservationPersistenceAdapterTests {

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @Test
    void shouldPersistStationHistoryWithoutOverwritingOlderEntries() {
        WeatherObservation firstObservation = observation(
            "Tallinn-Harku",
            "26038",
            "-5.10",
            "4.70",
            "Light snow",
            "2026-03-28T06:15:00Z"
        );
        WeatherObservation secondObservation = observation(
            "Tallinn-Harku",
            "26038",
            "-3.20",
            "6.50",
            "Cloudy",
            "2026-03-28T07:15:00Z"
        );

        WeatherObservation savedFirstObservation = weatherObservationRepository.save(firstObservation);
        WeatherObservation savedSecondObservation = weatherObservationRepository.save(secondObservation);

        assertThat(savedFirstObservation.id()).isNotNull();
        assertThat(savedSecondObservation.id()).isNotNull();
        assertThat(savedSecondObservation.id()).isNotEqualTo(savedFirstObservation.id());

        assertThat(weatherObservationRepository.findAllByStationNameOrderByObservedAtAsc("Tallinn-Harku"))
            .extracting(WeatherObservation::observedAt)
            .containsExactly(firstObservation.observedAt(), secondObservation.observedAt());

        assertThat(weatherObservationRepository.findLatestByStationName("Tallinn-Harku"))
            .get()
            .extracting(WeatherObservation::observedAt)
            .isEqualTo(secondObservation.observedAt());
    }

    @Test
    void shouldFindObservationByStationNameAndExactTimestamp() {
        WeatherObservation tartuObservation = observation(
            "Tartu-Toravere",
            "26242",
            "-2.10",
            "4.70",
            "Light snow shower",
            "2026-03-28T08:15:00Z"
        );
        WeatherObservation parnuObservation = observation(
            "Parnu",
            "41803",
            "1.00",
            "8.90",
            "Rain",
            "2026-03-28T08:15:00Z"
        );

        weatherObservationRepository.save(tartuObservation);
        weatherObservationRepository.save(parnuObservation);

        assertThat(weatherObservationRepository.findByStationNameAndObservedAt(
            "Tartu-Toravere",
            Instant.parse("2026-03-28T08:15:00Z")
        ))
            .get()
            .extracting(WeatherObservation::wmoCode, WeatherObservation::weatherPhenomenon)
            .containsExactly("26242", "Light snow shower");

        assertThat(weatherObservationRepository.findByStationNameAndObservedAt(
            "Tartu-Toravere",
            Instant.parse("2026-03-28T09:15:00Z")
        )).isEmpty();
    }

    private WeatherObservation observation(String stationName,
                                           String wmoCode,
                                           String airTemperature,
                                           String windSpeed,
                                           String weatherPhenomenon,
                                           String observedAt) {
        return new WeatherObservation(
            stationName,
            wmoCode,
            new BigDecimal(airTemperature),
            new BigDecimal(windSpeed),
            weatherPhenomenon,
            Instant.parse(observedAt)
        );
    }
}
