package com.example.inventoryservice.config;


import com.example.common.kafka.config.SwaggerConfigInterface;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig implements SwaggerConfigInterface {

    @Bean
    public GroupedOpenApi boardGroupedOpenApi() {
        return createGroupedOpenApi("inventory", "/api/inventory/**", "Inventory API", "재고 관리를 위한 API");
    }
}