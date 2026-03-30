package ee.fujitsu.fooddeliveryfeecalculator.api;

import ee.fujitsu.fooddeliveryfeecalculator.domain.model.WeatherObservation;
import ee.fujitsu.fooddeliveryfeecalculator.domain.port.WeatherObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class DeliveryFeeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherObservationRepository weatherObservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearWeatherObservationHistory() {
        jdbcTemplate.update("delete from weather_observation");
    }

    @Test
    void shouldReturnCalculatedDeliveryFeeUsingLatestWeatherObservation() throws Exception {
        weatherObservationRepository.save(observation(
            "Tallinn-Harku",
            "26038",
            "-12.0",
            "4.0",
            "Clear",
            "2026-03-30T19:15:00Z"
        ));
        weatherObservationRepository.save(observation(
            "Tallinn-Harku",
            "26038",
            "2.0",
            "4.0",
            "Clear",
            "2026-03-30T20:15:00Z"
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                .queryParam("city", "Tallinn")
                .queryParam("vehicleType", "bike"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Tallinn"))
            .andExpect(jsonPath("$.vehicleType").value("BIKE"))
            .andExpect(jsonPath("$.deliveryFee").value(4.0))
            .andExpect(jsonPath("$.observedAt").value("2026-03-30T20:15:00Z"));
    }

    @Test
    void shouldReturnValidationErrorForUnsupportedCity() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                .queryParam("city", "Narva")
                .queryParam("vehicleType", "car"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Unsupported city: Narva"))
            .andExpect(jsonPath("$.path").value("/api/v1/delivery-fee"));
    }

    @Test
    void shouldReturnValidationErrorWhenVehicleTypeParameterIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-fee")
                .queryParam("city", "Tallinn"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("Missing required request parameter: vehicleType"))
            .andExpect(jsonPath("$.path").value("/api/v1/delivery-fee"));
    }

    @Test
    void shouldReturnForbiddenErrorWhenWeatherMakesBikeUnsafe() throws Exception {
        weatherObservationRepository.save(observation(
            "Tartu-Tõravere",
            "26242",
            "3.0",
            "21.0",
            "Clear",
            "2026-03-30T20:15:00Z"
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                .queryParam("city", "Tartu")
                .queryParam("vehicleType", "bike"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.code").value("VEHICLE_USAGE_FORBIDDEN"))
            .andExpect(jsonPath("$.message").value("Usage of selected vehicle type is forbidden due to high wind speed."))
            .andExpect(jsonPath("$.path").value("/api/v1/delivery-fee"));
    }

    @Test
    void shouldReturnNotFoundWhenWeatherDataIsMissingForRequestedCity() throws Exception {
        weatherObservationRepository.save(observation(
            "Tallinn-Harku",
            "26038",
            "3.0",
            "4.0",
            "Clear",
            "2026-03-30T20:15:00Z"
        ));

        mockMvc.perform(get("/api/v1/delivery-fee")
                .queryParam("city", "Parnu")
                .queryParam("vehicleType", "scooter"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("WEATHER_DATA_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("No weather observation is available for city: Parnu"))
            .andExpect(jsonPath("$.path").value("/api/v1/delivery-fee"));
    }

    private WeatherObservation observation(String stationName,
                                           String wmoCode,
                                           String airTemperature,
                                           String windSpeed,
                                           String phenomenon,
                                           String observedAt) {
        return new WeatherObservation(
            stationName,
            wmoCode,
            new BigDecimal(airTemperature),
            new BigDecimal(windSpeed),
            phenomenon,
            Instant.parse(observedAt)
        );
    }
}
