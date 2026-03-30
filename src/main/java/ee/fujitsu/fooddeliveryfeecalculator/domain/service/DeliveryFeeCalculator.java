package ee.fujitsu.fooddeliveryfeecalculator.domain.service;

import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.VehicleUsageForbiddenException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.DeliveryFee;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.VehicleType;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

@Service
public class DeliveryFeeCalculator {

    private static final BigDecimal TALLINN_BASE_FEE = new BigDecimal("4.00");
    private static final BigDecimal TARTU_BASE_FEE = new BigDecimal("3.50");
    private static final BigDecimal PARNU_BASE_FEE = new BigDecimal("3.00");
    private static final BigDecimal HALF_EURO = new BigDecimal("0.50");
    private static final BigDecimal ONE_EURO = new BigDecimal("1.00");

    public DeliveryFee calculate(SupportedCity city,
                                 VehicleType vehicleType,
                                 WeatherObservation weatherObservation) {
        BigDecimal regionalBaseFee = regionalBaseFee(city);
        BigDecimal weatherExtraFee = weatherExtraFee(vehicleType, weatherObservation);

        return new DeliveryFee(
            city,
            vehicleType,
            regionalBaseFee,
            weatherExtraFee,
            regionalBaseFee.add(weatherExtraFee),
            weatherObservation.observedAt()
        );
    }

    private BigDecimal regionalBaseFee(SupportedCity city) {
        return switch (city) {
            case TALLINN -> TALLINN_BASE_FEE;
            case TARTU -> TARTU_BASE_FEE;
            case PARNU -> PARNU_BASE_FEE;
        };
    }

    private BigDecimal weatherExtraFee(VehicleType vehicleType, WeatherObservation weatherObservation) {
        if (vehicleType == VehicleType.CAR) {
            return BigDecimal.ZERO.setScale(2);
        }

        BigDecimal extraFee = temperatureExtraFee(weatherObservation.airTemperature());
        extraFee = extraFee.add(phenomenonExtraFee(vehicleType, weatherObservation.weatherPhenomenon()));

        if (vehicleType == VehicleType.BIKE) {
            extraFee = extraFee.add(windExtraFee(weatherObservation.windSpeed()));
        }

        return extraFee;
    }

    private BigDecimal temperatureExtraFee(BigDecimal airTemperature) {
        if (airTemperature.compareTo(new BigDecimal("-10")) < 0) {
            return ONE_EURO;
        }

        if (airTemperature.compareTo(BigDecimal.ZERO) <= 0) {
            return HALF_EURO;
        }

        return BigDecimal.ZERO.setScale(2);
    }

    private BigDecimal windExtraFee(BigDecimal windSpeed) {
        if (windSpeed.compareTo(new BigDecimal("20")) > 0) {
            throw new VehicleUsageForbiddenException("Usage of selected vehicle type is forbidden due to high wind speed.");
        }

        if (windSpeed.compareTo(new BigDecimal("10")) >= 0) {
            return HALF_EURO;
        }

        return BigDecimal.ZERO.setScale(2);
    }

    private BigDecimal phenomenonExtraFee(VehicleType vehicleType, String weatherPhenomenon) {
        String normalizedPhenomenon = weatherPhenomenon == null
            ? ""
            : weatherPhenomenon.trim().toLowerCase(Locale.ROOT);

        if (containsAny(normalizedPhenomenon, "glaze", "hail", "thunder")) {
            throw new VehicleUsageForbiddenException(
                "Usage of selected vehicle type is forbidden due to hazardous weather conditions."
            );
        }

        if (containsAny(normalizedPhenomenon, "snow", "sleet")) {
            return ONE_EURO;
        }

        if (normalizedPhenomenon.contains("rain")) {
            return HALF_EURO;
        }

        return BigDecimal.ZERO.setScale(2);
    }

    private boolean containsAny(String value, String... fragments) {
        for (String fragment : fragments) {
            if (value.contains(fragment)) {
                return true;
            }
        }

        return false;
    }
}
