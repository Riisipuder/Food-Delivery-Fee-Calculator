package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.scheduling;

import ee.fujitsu.fooddeliveryfeecalculator.application.WeatherObservationImportService;
import ee.fujitsu.fooddeliveryfeecalculator.configuration.WeatherImportProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherObservationImportSchedulerTests {

    @Mock
    private WeatherObservationImportService weatherObservationImportService;

    @Mock
    private WeatherImportProperties weatherImportProperties;

    @InjectMocks
    private WeatherObservationImportScheduler weatherObservationImportScheduler;

    @Test
    void shouldUseConfigurableCronPlaceholderOnScheduledMethod() throws NoSuchMethodException {
        Scheduled scheduledAnnotation = WeatherObservationImportScheduler.class
            .getMethod("runScheduledImport")
            .getAnnotation(Scheduled.class);

        assertThat(scheduledAnnotation).isNotNull();
        assertThat(scheduledAnnotation.cron()).isEqualTo("${weather.import.cron}");
    }

    @Test
    void shouldNotPropagateExceptionsFromScheduledImport() {
        when(weatherObservationImportService.importLatestObservations())
            .thenThrow(new IllegalStateException("simulated failure"));

        assertThatCode(() -> weatherObservationImportScheduler.runScheduledImport())
            .doesNotThrowAnyException();

        verify(weatherObservationImportService).importLatestObservations();
    }
}
