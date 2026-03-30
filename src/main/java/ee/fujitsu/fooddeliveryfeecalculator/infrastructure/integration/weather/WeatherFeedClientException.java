package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

public class WeatherFeedClientException extends RuntimeException {

    public WeatherFeedClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
