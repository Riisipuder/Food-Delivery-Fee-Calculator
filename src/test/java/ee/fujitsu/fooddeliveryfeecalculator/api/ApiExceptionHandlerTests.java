package ee.fujitsu.fooddeliveryfeecalculator.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTests {

    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new ExplodingController())
        .setControllerAdvice(new ApiExceptionHandler())
        .build();

    @Test
    void shouldReturnConsistentInternalErrorResponse() throws Exception {
        mockMvc.perform(get("/boom"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
            .andExpect(jsonPath("$.path").value("/boom"));
    }

    @RestController
    static class ExplodingController {

        @GetMapping("/boom")
        String explode() {
            throw new IllegalStateException("boom");
        }
    }
}
