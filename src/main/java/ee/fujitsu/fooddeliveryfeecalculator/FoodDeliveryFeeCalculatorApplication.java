package ee.fujitsu.fooddeliveryfeecalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FoodDeliveryFeeCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryFeeCalculatorApplication.class, args);
    }

}
