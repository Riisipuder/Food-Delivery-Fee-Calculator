package ee.fujitsu.fooddeliveryfeecalculator.domain.exception;

public class VehicleUsageForbiddenException extends RuntimeException {

    public VehicleUsageForbiddenException(String message) {
        super(message);
    }
}
