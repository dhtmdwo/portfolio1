// src/main/java/com/example/gatewayservice/controller/HealthAggregateController.java
package com.example.gatewayservice.config;

import com.example.gatewayservice.config.HealthProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aggregated")
public class HealthAggregateController {

    private final WebClient healthClient;
    private final List<HealthProperties.ServiceDescriptor> services;

    public HealthAggregateController(WebClient healthClient,
                                     HealthProperties props) {
        this.healthClient = healthClient;
        this.services = props.getServices();
    }

    @GetMapping("/health")
    public Mono<Map<String, Object>> aggregateHealth() {
        List<Mono<Map.Entry<String, Object>>> calls = services.stream()
                .map(this::callHealth)
                .collect(Collectors.toList());

        return Flux.merge(calls)                            // 병렬 호출 후 합침 :contentReference[oaicite:7]{index=7}
                .collectMap(Map.Entry::getKey,
                        Map.Entry::getValue);
    }

    private Mono<Map.Entry<String, Object>> callHealth(HealthProperties.ServiceDescriptor desc) {
        return healthClient.get()
                .uri(desc.getUrl())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})  // Map<String,Object> 바디 파싱 :contentReference[oaicite:8]{index=8}
                .map(new Function<Map<String,Object>, Map.Entry<String,Object>>() {
                    @Override
                    public Map.Entry<String, Object> apply(Map<String, Object> body) {
                        return new AbstractMap.SimpleEntry<>(desc.getName(), (Object) body);
                    }
                })
                .timeout(Duration.ofSeconds(5))                                       // 타임아웃 설정
                .onErrorResume(ex -> {
                    Map<String, Object> down = Map.of(
                            "status", "DOWN",
                            "error", ex.getMessage()
                    );
                    return Mono.just(new SimpleEntry<>(desc.getName(), (Object) down));
                });
    }
}
