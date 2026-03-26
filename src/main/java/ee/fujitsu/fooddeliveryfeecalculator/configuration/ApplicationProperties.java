package ee.fujitsu.fooddeliveryfeecalculator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(String displayName) {
}
