package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.InvalidRequestException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.WeatherDataNotFoundException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.DeliveryFee;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedWeatherStation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import ee.fujitsu.fooddeliveryfeecalculator.domain.service.DeliveryFeeCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliveryFeeServiceTests {

    private final WeatherObservationRepository weatherObservationRepository = mock(WeatherObservationRepository.class);
    private final DeliveryFeeService deliveryFeeService = new DeliveryFeeService(
        weatherObservationRepository,
        new DeliveryFeeCalculator()
    );

    @Test
    void shouldAcceptNormalizedCityAndVehicleAliases() {
        when(weatherObservationRepository.findLatestByStationName(SupportedWeatherStation.PARNU.stationName()))
            .thenReturn(Optional.of(observation(SupportedWeatherStation.PARNU.stationName(), "41803", "-2.0", "4.0", "Light rain")));

        DeliveryFee deliveryFee = deliveryFeeService.calculateDeliveryFee("p\u00E4rnu", "bicycle");

        assertThat(deliveryFee.city()).isEqualTo(SupportedCity.PARNU);
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.00");
        verify(weatherObservationRepository).findLatestByStationName(SupportedWeatherStation.PARNU.stationName());
    }

    @Test
    void shouldRejectUnsupportedVehicleType() {
        assertThatThrownBy(() -> deliveryFeeService.calculateDeliveryFee("Tallinn", "hoverboard"))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage("Unsupported vehicle type: hoverboard");
    }

    @Test
    void shouldFailWhenNoWeatherObservationExistsForRequestedCity() {
        when(weatherObservationRepository.findLatestByStationName(SupportedWeatherStation.TARTU_TORAVERE.stationName()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryFeeService.calculateDeliveryFee("Tartu", "scooter"))
            .isInstanceOf(WeatherDataNotFoundException.class)
            .hasMessage("No weather observation is available for city: Tartu");
    }

    private WeatherObservation observation(String stationName,
                                           String wmoCode,
                                           String airTemperature,
                                           String windSpeed,
                                           String phenomenon) {
        return new WeatherObservation(
            stationName,
            wmoCode,
            new BigDecimal(airTemperature),
            new BigDecimal(windSpeed),
            phenomenon,
            Instant.parse("2026-03-30T20:15:00Z")
        );
    }
}
