package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationFeedClient;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeatherObservationImportService {

    private final WeatherObservationFeedClient weatherObservationFeedClient;
    private final WeatherObservationRepository weatherObservationRepository;

    public WeatherObservationImportService(WeatherObservationFeedClient weatherObservationFeedClient,
                                           WeatherObservationRepository weatherObservationRepository) {
        this.weatherObservationFeedClient = weatherObservationFeedClient;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    @Transactional
    public int importLatestObservations() {
        List<WeatherFeedObservation> feedObservations = weatherObservationFeedClient.fetchSupportedObservations();

        for (WeatherFeedObservation feedObservation : feedObservations) {
            weatherObservationRepository.save(toWeatherObservation(feedObservation));
        }

        return feedObservations.size();
    }

    private WeatherObservation toWeatherObservation(WeatherFeedObservation feedObservation) {
        return new WeatherObservation(
            feedObservation.stationName(),
            feedObservation.wmoCode(),
            feedObservation.airTemperature(),
            feedObservation.windSpeed(),
            feedObservation.weatherPhenomenon(),
            feedObservation.observedAt()
        );
    }
}
