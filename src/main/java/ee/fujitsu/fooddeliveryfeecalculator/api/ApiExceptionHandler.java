package ee.fujitsu.fooddeliveryfeecalculator.api;

import ee.fujitsu.fooddeliveryfeecalculator.api.model.ApiErrorResponse;
import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.InvalidRequestException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.VehicleUsageForbiddenException;
import ee.fujitsu.fooddeliveryfeecalculator.domain.exception.WeatherDataNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(InvalidRequestException exception,
                                                                 HttpServletRequest request) {
        return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestParameter(MissingServletRequestParameterException exception,
                                                                          HttpServletRequest request) {
        String message = "Missing required request parameter: " + exception.getParameterName();
        return errorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler(VehicleUsageForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleVehicleUsageForbidden(VehicleUsageForbiddenException exception,
                                                                        HttpServletRequest request) {
        return errorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "VEHICLE_USAGE_FORBIDDEN", exception.getMessage(), request);
    }

    @ExceptionHandler(WeatherDataNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWeatherDataNotFound(WeatherDataNotFoundException exception,
                                                                      HttpServletRequest request) {
        return errorResponse(HttpStatus.NOT_FOUND, "WEATHER_DATA_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(Exception exception,
                                                                      HttpServletRequest request) {
        log.error("Unhandled API exception", exception);
        return errorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred.",
            request
        );
    }

    private ResponseEntity<ApiErrorResponse> errorResponse(HttpStatus status,
                                                           String code,
                                                           String message,
                                                           HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            code,
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }
}
