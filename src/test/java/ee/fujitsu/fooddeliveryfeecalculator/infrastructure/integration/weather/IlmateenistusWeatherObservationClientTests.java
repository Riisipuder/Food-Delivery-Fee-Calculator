package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

import ee.fujitsu.fooddeliveryfeecalculator.configuration.WeatherApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class IlmateenistusWeatherObservationClientTests {

    private static final MediaType XML_UTF8 = MediaType.parseMediaType("application/xml;charset=UTF-8");

    @Test
    void shouldFetchAndParseSupportedObservationsFromConfiguredUrl() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        server.expect(requestTo("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php"))
            .andRespond(withSuccess("""
                <?xml version="1.0" encoding="UTF-8"?>
                <observations timestamp="1774901077">
                    <station>
                        <name>Tallinn-Harku</name>
                        <wmocode>26038</wmocode>
                        <airtemperature>-0.4</airtemperature>
                        <windspeed>0.4</windspeed>
                        <phenomenon>Overcast</phenomenon>
                    </station>
                    <station>
                        <name>Tartu-Tõravere</name>
                        <wmocode>26242</wmocode>
                        <airtemperature>5.7</airtemperature>
                        <windspeed>0.6</windspeed>
                        <phenomenon>Cloudy with clear spells</phenomenon>
                    </station>
                    <station>
                        <name>Pärnu</name>
                        <wmocode>41803</wmocode>
                        <airtemperature>3.8</airtemperature>
                        <windspeed>2.4</windspeed>
                        <phenomenon>Light rain</phenomenon>
                    </station>
                </observations>
                """, XML_UTF8));

        IlmateenistusWeatherObservationClient client = new IlmateenistusWeatherObservationClient(
            restClientBuilder,
            new WeatherApiProperties("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php"),
            new IlmateenistusXmlParser()
        );

        assertThat(client.fetchSupportedObservations()).hasSize(3);
        server.verify();
    }

    @Test
    void shouldFailFastOnMalformedXml() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        server.expect(requestTo("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php"))
            .andRespond(withSuccess("<observations>", XML_UTF8));

        IlmateenistusWeatherObservationClient client = new IlmateenistusWeatherObservationClient(
            restClientBuilder,
            new WeatherApiProperties("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php"),
            new IlmateenistusXmlParser()
        );

        assertThatThrownBy(client::fetchSupportedObservations)
            .isInstanceOf(WeatherFeedParsingException.class);
    }
}
