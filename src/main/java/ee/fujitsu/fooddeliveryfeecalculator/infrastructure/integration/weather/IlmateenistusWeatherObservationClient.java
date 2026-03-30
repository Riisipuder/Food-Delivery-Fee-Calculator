package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

import ee.fujitsu.fooddeliveryfeecalculator.configuration.WeatherApiProperties;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationFeedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class IlmateenistusWeatherObservationClient implements WeatherObservationFeedClient {

    private final RestClient restClient;
    private final WeatherApiProperties weatherApiProperties;
    private final IlmateenistusXmlParser xmlParser;

    public IlmateenistusWeatherObservationClient(RestClient.Builder restClientBuilder,
                                                 WeatherApiProperties weatherApiProperties,
                                                 IlmateenistusXmlParser xmlParser) {
        this.restClient = restClientBuilder.build();
        this.weatherApiProperties = weatherApiProperties;
        this.xmlParser = xmlParser;
    }

    @Override
    public List<WeatherFeedObservation> fetchSupportedObservations() {
        try {
            String xmlPayload = restClient.get()
                .uri(weatherApiProperties.observationsUrl())
                .retrieve()
                .body(String.class);

            if (xmlPayload == null || xmlPayload.isBlank()) {
                throw new WeatherFeedClientException("The Ilmateenistus XML feed returned an empty body", null);
            }

            return xmlParser.parse(xmlPayload);
        } catch (WeatherFeedParsingException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new WeatherFeedClientException("Failed to fetch the Ilmateenistus XML feed", exception);
        }
    }
}
