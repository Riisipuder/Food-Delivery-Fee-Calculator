package ee.fujitsu.fooddeliveryfeecalculator.api;

import ee.fujitsu.fooddeliveryfeecalculator.application.SystemStatusService;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.ApplicationStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemStatusController {

    private final SystemStatusService systemStatusService;

    public SystemStatusController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }

    @GetMapping("/status")
    public ApplicationStatus getStatus() {
        return systemStatusService.getStatus();
    }
}
