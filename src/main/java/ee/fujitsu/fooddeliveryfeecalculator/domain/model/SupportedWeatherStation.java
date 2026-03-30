package ee.fujitsu.fooddeliveryfeecalculator.domain.model;

import java.util.Arrays;
import java.util.Optional;

public enum SupportedWeatherStation {
    TALLINN_HARKU(SupportedCity.TALLINN, "Tallinn-Harku", "26038"),
    TARTU_TORAVERE(SupportedCity.TARTU, "Tartu-Tõravere", "26242"),
    PARNU(SupportedCity.PARNU, "Pärnu", "41803");

    private final SupportedCity city;
    private final String stationName;
    private final String wmoCode;

    SupportedWeatherStation(SupportedCity city, String stationName, String wmoCode) {
        this.city = city;
        this.stationName = stationName;
        this.wmoCode = wmoCode;
    }

    public SupportedCity city() {
        return city;
    }

    public String stationName() {
        return stationName;
    }

    public String wmoCode() {
        return wmoCode;
    }

    public static Optional<SupportedWeatherStation> fromStationName(String stationName) {
        return Arrays.stream(values())
            .filter(station -> station.stationName.equals(stationName))
            .findFirst();
    }
}
