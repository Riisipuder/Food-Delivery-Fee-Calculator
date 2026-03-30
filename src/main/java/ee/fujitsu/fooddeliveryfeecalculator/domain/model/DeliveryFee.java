package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record DeliveryFee(
    SupportedCity city,
    VehicleType vehicleType,
    BigDecimal regionalBaseFee,
    BigDecimal weatherExtraFee,
    BigDecimal totalFee,
    Instant observedAt
) {

    public DeliveryFee {
        Objects.requireNonNull(city, "city must not be null");
        Objects.requireNonNull(vehicleType, "vehicleType must not be null");
        Objects.requireNonNull(regionalBaseFee, "regionalBaseFee must not be null");
        Objects.requireNonNull(weatherExtraFee, "weatherExtraFee must not be null");
        Objects.requireNonNull(totalFee, "totalFee must not be null");
        Objects.requireNonNull(observedAt, "observedAt must not be null");
    }
}
