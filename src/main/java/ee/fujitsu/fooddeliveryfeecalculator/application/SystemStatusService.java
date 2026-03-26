package ee.fujitsu.fooddeliveryfeecalculator.application;

import ee.fujitsu.fooddeliveryfeecalculator.configuration.ApplicationProperties;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.ApplicationStatus;
import ee.fujitsu.fooddeliveryfeecalculator.infrastructure.persistence.DatabaseHealthRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemStatusService {

    private final ApplicationProperties applicationProperties;
    private final DatabaseHealthRepository databaseHealthRepository;

    public SystemStatusService(ApplicationProperties applicationProperties,
                               DatabaseHealthRepository databaseHealthRepository) {
        this.applicationProperties = applicationProperties;
        this.databaseHealthRepository = databaseHealthRepository;
    }

    public ApplicationStatus getStatus() {
        String databaseStatus = databaseHealthRepository.isReachable() ? "UP" : "DOWN";

        return new ApplicationStatus(
            applicationProperties.displayName(),
            "UP",
            databaseStatus
        );
    }
}
