package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum VehicleType {
    CAR,
    SCOOTER,
    BIKE;

    public static Optional<VehicleType> from(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String normalizedInput = value.trim().toUpperCase(Locale.ROOT);

        if ("BICYCLE".equals(normalizedInput)) {
            return Optional.of(BIKE);
        }

        return Arrays.stream(values())
            .filter(vehicleType -> vehicleType.name().equals(normalizedInput))
            .findFirst();
    }
}
