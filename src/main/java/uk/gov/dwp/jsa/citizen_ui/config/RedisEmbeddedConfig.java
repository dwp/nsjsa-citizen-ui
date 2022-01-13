package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisEmbeddedConfig {

    @Bean
    @ConfigurationProperties(prefix = "redis")
    public RedisServerBean redisServer() {
        return new RedisServerBean();
    }
}
