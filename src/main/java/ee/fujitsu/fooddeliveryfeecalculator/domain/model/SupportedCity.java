package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum SupportedCity {
    TALLINN("Tallinn"),
    TARTU("Tartu"),
    PARNU("Parnu");

    private final String displayName;

    SupportedCity(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static Optional<SupportedCity> from(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String normalizedInput = normalize(value);

        return Arrays.stream(values())
            .filter(city -> normalize(city.name()).equals(normalizedInput)
                || normalize(city.displayName).equals(normalizedInput))
            .findFirst();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "")
            .replaceAll("[\\s_-]+", "")
            .toUpperCase(Locale.ROOT);
    }
}
