package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationFeedClient;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
class WeatherObservationImportServiceIntegrationTests {

    @Autowired
    private WeatherObservationImportService weatherObservationImportService;

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private WeatherObservationFeedClient weatherObservationFeedClient;

    @BeforeEach
    void clearWeatherObservationHistory() {
        jdbcTemplate.update("delete from weather_observation");
    }

    @Test
    void shouldPersistImportedObservationsIntoWeatherHistory() {
        when(weatherObservationFeedClient.fetchSupportedObservations()).thenReturn(List.of(
            feedObservation("Tallinn-Harku", "26038", "-0.4", "0.4", "Overcast"),
            feedObservation("Tartu-Tõravere", "26242", "5.7", "0.6", "Cloudy with clear spells"),
            feedObservation("Pärnu", "41803", "3.8", "2.4", "Light rain")
        ));

        int importedObservationCount = weatherObservationImportService.importLatestObservations();

        assertThat(importedObservationCount).isEqualTo(3);
        assertThat(weatherObservationRepository.findLatestByStationName("Tallinn-Harku")).isPresent();
        assertThat(weatherObservationRepository.findLatestByStationName("Tartu-Tõravere")).isPresent();
        assertThat(weatherObservationRepository.findLatestByStationName("Pärnu")).isPresent();
    }

    @Test
    void shouldRollbackTheImportWhenPersistenceFailsMidFlight() {
        when(weatherObservationFeedClient.fetchSupportedObservations()).thenReturn(List.of(
            feedObservation("Tallinn-Harku", "26038", "-0.4", "0.4", "Overcast"),
            feedObservation("Tartu-Tõravere-Station-Name-That-Is-Deliberately-Much-Longer-Than-One-Hundred-Characters-To-Trigger-A-Database-Failure",
                "26242", "5.7", "0.6", "Cloudy with clear spells"),
            feedObservation("Pärnu", "41803", "3.8", "2.4", "Light rain")
        ));

        assertThatThrownBy(() -> weatherObservationImportService.importLatestObservations())
            .isInstanceOf(RuntimeException.class);

        assertThat(weatherObservationRepository.findAllByStationNameOrderByObservedAtAsc("Tallinn-Harku")).isEmpty();
        assertThat(weatherObservationRepository.findAllByStationNameOrderByObservedAtAsc("Pärnu")).isEmpty();
    }

    private WeatherFeedObservation feedObservation(String stationName,
                                                   String wmoCode,
                                                   String airTemperature,
                                                   String windSpeed,
                                                   String phenomenon) {
        SupportedCity city = switch (stationName) {
            case "Tallinn-Harku" -> SupportedCity.TALLINN;
            case "Pärnu" -> SupportedCity.PARNU;
            default -> SupportedCity.TARTU;
        };

        return new WeatherFeedObservation(
            city,
            stationName,
            wmoCode,
            new BigDecimal(airTemperature),
            new BigDecimal(windSpeed),
            phenomenon,
            Instant.parse("2026-03-30T20:15:00Z")
        );
    }
}
