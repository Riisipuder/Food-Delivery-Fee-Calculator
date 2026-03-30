package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.InvalidRequestException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.WeatherDataNotFoundException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.DeliveryFee;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedWeatherStation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.VehicleType;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import ee.fujitsu.fooddeliveryfeecalculator.domain.service.DeliveryFeeCalculator;
import org.springframework.stereotype.Service;

@Service
public class DeliveryFeeService {

    private final WeatherObservationRepository weatherObservationRepository;
    private final DeliveryFeeCalculator deliveryFeeCalculator;

    public DeliveryFeeService(WeatherObservationRepository weatherObservationRepository,
                              DeliveryFeeCalculator deliveryFeeCalculator) {
        this.weatherObservationRepository = weatherObservationRepository;
        this.deliveryFeeCalculator = deliveryFeeCalculator;
    }

    public DeliveryFee calculateDeliveryFee(String cityValue, String vehicleTypeValue) {
        SupportedCity city = SupportedCity.from(cityValue)
            .orElseThrow(() -> new InvalidRequestException("Unsupported city: " + cityValue));

        VehicleType vehicleType = VehicleType.from(vehicleTypeValue)
            .orElseThrow(() -> new InvalidRequestException("Unsupported vehicle type: " + vehicleTypeValue));

        SupportedWeatherStation supportedWeatherStation = SupportedWeatherStation.fromCity(city)
            .orElseThrow(() -> new InvalidRequestException("Unsupported city: " + cityValue));

        WeatherObservation latestObservation = weatherObservationRepository
            .findLatestByStationName(supportedWeatherStation.stationName())
            .orElseThrow(() -> new WeatherDataNotFoundException(
                "No weather observation is available for city: " + city.displayName()
            ));

        return deliveryFeeCalculator.calculate(city, vehicleType, latestObservation);
    }
}
