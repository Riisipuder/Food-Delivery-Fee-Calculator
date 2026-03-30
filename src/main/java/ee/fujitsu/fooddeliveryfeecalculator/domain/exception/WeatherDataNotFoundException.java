package ee.fujitsu.fooddeliveryfeecalculator.domain.exception;

public class WeatherDataNotFoundException extends RuntimeException {

    public WeatherDataNotFoundException(String message) {
        super(message);
    }
}
