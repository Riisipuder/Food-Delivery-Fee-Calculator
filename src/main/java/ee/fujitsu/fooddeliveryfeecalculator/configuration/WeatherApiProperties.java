package ee.fujitsu.fooddeliveryfeecalculator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(String observationsUrl) {
}
