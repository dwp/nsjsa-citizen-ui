package uk.gov.dwp.jsa.citizen_ui.config;

import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import static uk.gov.dwp.jsa.citizen_ui.Constants.Environment.LOCAL;
import static uk.gov.dwp.jsa.citizen_ui.Constants.Environment.TEST;
import static uk.gov.dwp.jsa.citizen_ui.Constants.Environment.DEV;

/**
 *
 * Handles configuration for Redis client.
 */
@Configuration
@Log
@Profile({LOCAL, TEST, DEV})
public class RedisConfig {

    @Bean
    @ConfigurationProperties("redis")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setValueSerializer(new GenericToStringSerializer<>(String.class));
        RedisStandaloneConfiguration standaloneConfig = redisStandaloneConfiguration();
        template.setConnectionFactory(new JedisConnectionFactory(standaloneConfig));
        return template;
    }
}
