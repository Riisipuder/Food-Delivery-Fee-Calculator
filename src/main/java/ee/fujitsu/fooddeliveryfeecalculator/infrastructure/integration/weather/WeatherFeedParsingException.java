package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.integration.weather;

public class WeatherFeedParsingException extends RuntimeException {

    public WeatherFeedParsingException(String message) {
        super(message);
    }

    public WeatherFeedParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
