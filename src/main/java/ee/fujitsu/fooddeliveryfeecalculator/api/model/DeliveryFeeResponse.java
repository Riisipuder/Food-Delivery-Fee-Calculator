package ee.fujitsu.fooddeliveryfeecalculator.api.model;

import java.math.BigDecimal;
import java.time.Instant;

public record DeliveryFeeResponse(
    String city,
    String vehicleType,
    BigDecimal deliveryFee,
    Instant observedAt
) {
}
