package com.example.be12fin5verdosewmthisbe.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.redis.sentinel")
@Getter
@Setter
public class RedisSentinelProperties {
    private String master;
    private List<String> nodes;
    private String password;
}