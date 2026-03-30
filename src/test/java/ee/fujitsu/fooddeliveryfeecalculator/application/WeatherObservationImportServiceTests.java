package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationFeedClient;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherObservationImportServiceTests {

    @Mock
    private WeatherObservationFeedClient weatherObservationFeedClient;

    @Mock
    private WeatherObservationRepository weatherObservationRepository;

    @InjectMocks
    private WeatherObservationImportService weatherObservationImportService;

    @Test
    void shouldImportAllSupportedFeedObservationsIntoWeatherHistory() {
        List<WeatherFeedObservation> feedObservations = List.of(
            new WeatherFeedObservation(
                SupportedCity.TALLINN,
                "Tallinn-Harku",
                "26038",
                new BigDecimal("-1.2"),
                new BigDecimal("3.4"),
                "Overcast",
                Instant.parse("2026-03-30T20:15:00Z")
            ),
            new WeatherFeedObservation(
                SupportedCity.TARTU,
                "Tartu-Tõravere",
                "26242",
                new BigDecimal("0.7"),
                new BigDecimal("1.6"),
                "Cloudy with clear spells",
                Instant.parse("2026-03-30T20:15:00Z")
            ),
            new WeatherFeedObservation(
                SupportedCity.PARNU,
                "Pärnu",
                "41803",
                new BigDecimal("1.8"),
                new BigDecimal("2.4"),
                "Light rain",
                Instant.parse("2026-03-30T20:15:00Z")
            )
        );

        when(weatherObservationFeedClient.fetchSupportedObservations()).thenReturn(feedObservations);

        int importedObservationCount = weatherObservationImportService.importLatestObservations();

        ArgumentCaptor<WeatherObservation> savedObservationCaptor = ArgumentCaptor.forClass(WeatherObservation.class);
        verify(weatherObservationRepository, times(3)).save(savedObservationCaptor.capture());

        assertThat(importedObservationCount).isEqualTo(3);
        assertThat(savedObservationCaptor.getAllValues())
            .extracting(WeatherObservation::stationName, WeatherObservation::wmoCode, WeatherObservation::observedAt)
            .containsExactly(
                org.assertj.core.groups.Tuple.tuple("Tallinn-Harku", "26038", Instant.parse("2026-03-30T20:15:00Z")),
                org.assertj.core.groups.Tuple.tuple("Tartu-Tõravere", "26242", Instant.parse("2026-03-30T20:15:00Z")),
                org.assertj.core.groups.Tuple.tuple("Pärnu", "41803", Instant.parse("2026-03-30T20:15:00Z"))
            );
    }
}
