package com.example.gatewayservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "health")
public class HealthProperties {
    private List<ServiceDescriptor> services = new ArrayList<>();

    @Data
    public static class ServiceDescriptor {
        private String name;
        private String url;
    }
}
