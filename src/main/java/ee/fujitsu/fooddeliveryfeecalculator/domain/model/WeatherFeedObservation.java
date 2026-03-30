package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record WeatherFeedObservation(
    SupportedCity city,
    String stationName,
    String wmoCode,
    BigDecimal airTemperature,
    BigDecimal windSpeed,
    String weatherPhenomenon,
    Instant observedAt
) {

    public WeatherFeedObservation {
        Objects.requireNonNull(city, "city must not be null");
        Objects.requireNonNull(stationName, "stationName must not be null");
        Objects.requireNonNull(wmoCode, "wmoCode must not be null");
        Objects.requireNonNull(airTemperature, "airTemperature must not be null");
        Objects.requireNonNull(windSpeed, "windSpeed must not be null");
        Objects.requireNonNull(weatherPhenomenon, "weatherPhenomenon must not be null");
        Objects.requireNonNull(observedAt, "observedAt must not be null");
    }
}
