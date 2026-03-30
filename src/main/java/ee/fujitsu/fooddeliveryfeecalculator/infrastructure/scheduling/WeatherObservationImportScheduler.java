package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.scheduling;

import ee.fujitsu.fooddeliveryfeecalculator.application.WeatherObservationImportService;
import ee.fujitsu.fooddeliveryfeecalculator.configuration.WeatherImportProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherObservationImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherObservationImportScheduler.class);

    private final WeatherObservationImportService weatherObservationImportService;
    private final WeatherImportProperties weatherImportProperties;

    public WeatherObservationImportScheduler(WeatherObservationImportService weatherObservationImportService,
                                            WeatherImportProperties weatherImportProperties) {
        this.weatherObservationImportService = weatherObservationImportService;
        this.weatherImportProperties = weatherImportProperties;
    }

    @Scheduled(cron = "${weather.import.cron}")
    public void runScheduledImport() {
        try {
            int importedObservationCount = weatherObservationImportService.importLatestObservations();
            log.info(
                "Scheduled weather import completed successfully. Imported {} observations using cron '{}'.",
                importedObservationCount,
                weatherImportProperties.cron()
            );
        } catch (RuntimeException exception) {
            log.error("Scheduled weather import failed.", exception);
        }
    }
}
