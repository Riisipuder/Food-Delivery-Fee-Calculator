package ee.fujitsu.fooddeliveryfeecalculator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.import")
public record WeatherImportProperties(String cron) {
}
