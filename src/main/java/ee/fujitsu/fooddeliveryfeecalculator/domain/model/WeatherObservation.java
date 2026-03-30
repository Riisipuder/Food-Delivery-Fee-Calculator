package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record WeatherObservation(
    Long id,
    String stationName,
    String wmoCode,
    BigDecimal airTemperature,
    BigDecimal windSpeed,
    String weatherPhenomenon,
    Instant observedAt
) {

    public WeatherObservation {
        Objects.requireNonNull(stationName, "stationName must not be null");
        Objects.requireNonNull(wmoCode, "wmoCode must not be null");
        Objects.requireNonNull(airTemperature, "airTemperature must not be null");
        Objects.requireNonNull(windSpeed, "windSpeed must not be null");
        Objects.requireNonNull(observedAt, "observedAt must not be null");
    }

    public WeatherObservation(String stationName,
                              String wmoCode,
                              BigDecimal airTemperature,
                              BigDecimal windSpeed,
                              String weatherPhenomenon,
                              Instant observedAt) {
        this(null, stationName, wmoCode, airTemperature, windSpeed, weatherPhenomenon, observedAt);
    }
}
