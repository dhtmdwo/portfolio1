package com.example.be12fin5verdosewmthisbe.config;

import com.example.be12fin5verdosewmthisbe.redis.RedisSentinelProperties;
import com.example.be12fin5verdosewmthisbe.store.model.dto.StoreDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisSentinelProperties sentinelProps;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(sentinelProps.getSentinel().getMaster());

        for (String node : sentinelProps.getSentinel().getNodes()) {
            String[] parts = node.split(":");
            config.sentinel(parts[0], Integer.parseInt(parts[1]));
        }

        config.setPassword(RedisPassword.of(sentinelProps.getPassword()));

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> defaultRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, List<StoreDto.response>> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<StoreDto.response>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ObjectMapper 설정 (Java8 시간 API 지원)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // List<StoreDto.response> 타입을 직접 설정하여 Jackson2JsonRedisSerializer 생성
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, StoreDto.response.class);
        Jackson2JsonRedisSerializer<List<StoreDto.response>> serializer =
                new Jackson2JsonRedisSerializer<>(javaType);

        // ObjectMapper 설정
        serializer.setObjectMapper(objectMapper);

        // Key와 Value에 맞는 직렬화 설정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}