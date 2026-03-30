package ee.fujitsu.fooddeliveryfeecalculator.api;

import ee.fujitsu.fooddeliveryfeecalculator.api.model.DeliveryFeeResponse;
import ee.fujitsu.fooddeliveryfeecalculator.application.DeliveryFeeService;
import ee.fujitsu.fooddeliveryfeecalculator.domain.model.DeliveryFee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping
    public DeliveryFeeResponse calculateDeliveryFee(@RequestParam String city,
                                                    @RequestParam String vehicleType) {
        DeliveryFee deliveryFee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);

        return new DeliveryFeeResponse(
            deliveryFee.city().displayName(),
            deliveryFee.vehicleType().name(),
            deliveryFee.totalFee(),
            deliveryFee.observedAt()
        );
    }
}
