package ee.fujitsu.fooddeliveryfeecalculator.domain.service;

import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.VehicleUsageForbiddenException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.DeliveryFee;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.VehicleType;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryFeeCalculatorTests {

    private final DeliveryFeeCalculator deliveryFeeCalculator = new DeliveryFeeCalculator();

    @Test
    void shouldApplyRegionalBaseFeeForCarWithoutWeatherExtras() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.TALLINN,
            VehicleType.CAR,
            observation("-5.0", "15.0", "Heavy snowfall")
        );

        assertThat(deliveryFee.regionalBaseFee()).isEqualByComparingTo("4.00");
        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("0.00");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.00");
    }

    @Test
    void shouldApplyAirTemperatureAndPhenomenonExtraFeeForScooter() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.TARTU,
            VehicleType.SCOOTER,
            observation("-2.0", "4.0", "Light rain")
        );

        assertThat(deliveryFee.regionalBaseFee()).isEqualByComparingTo("3.50");
        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("1.00");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.50");
    }

    @Test
    void shouldApplyHalfEuroExtraAtTemperatureBoundaryOfMinusTenDegrees() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.TARTU,
            VehicleType.SCOOTER,
            observation("-10.0", "4.0", "Clear")
        );

        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("0.50");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.00");
    }

    @Test
    void shouldApplyAirTemperatureWindAndPhenomenonExtraFeeForBike() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.PARNU,
            VehicleType.BIKE,
            observation("-12.0", "12.0", "Light snow shower")
        );

        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("2.50");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("5.50");
    }

    @Test
    void shouldApplyWindExtraFeeAtBoundaryOfTenMetersPerSecond() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.TALLINN,
            VehicleType.BIKE,
            observation("3.0", "10.0", "Clear")
        );

        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("0.50");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.50");
    }

    @Test
    void shouldAllowBikeAtBoundaryOfTwentyMetersPerSecond() {
        DeliveryFee deliveryFee = deliveryFeeCalculator.calculate(
            SupportedCity.TALLINN,
            VehicleType.BIKE,
            observation("3.0", "20.0", "Clear")
        );

        assertThat(deliveryFee.weatherExtraFee()).isEqualByComparingTo("0.50");
        assertThat(deliveryFee.totalFee()).isEqualByComparingTo("4.50");
    }

    @Test
    void shouldForbidBikeWhenWindSpeedExceedsTwentyMetersPerSecond() {
        assertThatThrownBy(() -> deliveryFeeCalculator.calculate(
            SupportedCity.TALLINN,
            VehicleType.BIKE,
            observation("3.0", "21.0", "Clear")
        ))
            .isInstanceOf(VehicleUsageForbiddenException.class)
            .hasMessage("Usage of selected vehicle type is forbidden due to high wind speed.");
    }

    @Test
    void shouldForbidScooterWhenPhenomenonIsHazardous() {
        assertThatThrownBy(() -> deliveryFeeCalculator.calculate(
            SupportedCity.TARTU,
            VehicleType.SCOOTER,
            observation("1.0", "4.0", "Hail")
        ))
            .isInstanceOf(VehicleUsageForbiddenException.class)
            .hasMessage("Usage of selected vehicle type is forbidden due to hazardous weather conditions.");
    }

    private WeatherObservation observation(String airTemperature, String windSpeed, String phenomenon) {
        return new WeatherObservation(
            "Tallinn-Harku",
            "26038",
            new BigDecimal(airTemperature),
            new BigDecimal(windSpeed),
            phenomenon,
            Instant.parse("2026-03-30T20:15:00Z")
        );
    }
}
