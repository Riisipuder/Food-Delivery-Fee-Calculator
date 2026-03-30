package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.SupportedCity;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherFeedObservation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IlmateenistusXmlParserTests {

    private final IlmateenistusXmlParser xmlParser = new IlmateenistusXmlParser();

    @Test
    void shouldParseSupportedStationsFromTheOfficialFeedShape() {
        List<WeatherFeedObservation> observations = xmlParser.parse("""
            <?xml version="1.0" encoding="UTF-8"?>
            <observations timestamp="1774901077">
                <station>
                    <name>Kuressaare linn</name>
                    <wmocode></wmocode>
                    <airtemperature>-2.3</airtemperature>
                    <windspeed></windspeed>
                    <phenomenon></phenomenon>
                </station>
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
            """);

        assertThat(observations).hasSize(3);
        assertThat(observations)
            .extracting(WeatherFeedObservation::city)
            .containsExactly(SupportedCity.TALLINN, SupportedCity.TARTU, SupportedCity.PARNU);
        assertThat(observations)
            .extracting(WeatherFeedObservation::stationName)
            .containsExactly("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");
        assertThat(observations)
            .extracting(WeatherFeedObservation::wmoCode)
            .containsExactly("26038", "26242", "41803");
        assertThat(observations)
            .extracting(WeatherFeedObservation::weatherPhenomenon)
            .containsExactly("Overcast", "Cloudy with clear spells", "Light rain");
        assertThat(observations)
            .extracting(observation -> observation.observedAt().getEpochSecond())
            .containsOnly(1774901077L);
    }

    @Test
    void shouldFailWhenARequiredSupportedStationIsMissing() {
        assertThatThrownBy(() -> xmlParser.parse("""
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
            </observations>
            """))
            .isInstanceOf(WeatherFeedParsingException.class)
            .hasMessageContaining("Missing required supported station");
    }

    @Test
    void shouldFailWhenARequiredSupportedFieldCannotBeParsed() {
        assertThatThrownBy(() -> xmlParser.parse("""
            <observations timestamp="1774901077">
                <station>
                    <name>Tallinn-Harku</name>
                    <wmocode>26038</wmocode>
                    <airtemperature>not-a-number</airtemperature>
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
            """))
            .isInstanceOf(WeatherFeedParsingException.class)
            .hasMessageContaining("Invalid decimal value");
    }
}
