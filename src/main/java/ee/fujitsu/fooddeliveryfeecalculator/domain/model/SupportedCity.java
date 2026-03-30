package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

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
}
