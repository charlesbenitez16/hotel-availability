package com.mindata.hotelavailability.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelAvailabilityOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Availability Search API")
                        .description("Registers hotel availability searches and reports how many "
                                + "identical searches (same hotel, dates and ages, in the same order) "
                                + "have been made.")
                        .version("1.0.0")
                        .contact(new Contact().name("Mindata Backend Challenge")));
    }
}
