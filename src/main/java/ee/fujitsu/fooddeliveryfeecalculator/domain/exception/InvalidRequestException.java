package ee.fujitsu.fooddeliveryfeecalculator.domain.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
