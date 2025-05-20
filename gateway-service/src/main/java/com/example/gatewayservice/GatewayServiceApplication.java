package com.example.gatewayservice;

import com.example.gatewayservice.config.HealthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(
		scanBasePackages = {
				"com.example.gatewayservice",
				"com.example.common.common"
		}
)
@EnableConfigurationProperties(HealthProperties.class)
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

}
