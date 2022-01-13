package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import java.time.Duration;
import java.util.Collections;

@Configuration
@Profile({"aws & !dev"})
@ConfigurationProperties("spring.redis.cluster")
public class LettuceClusterConfig {
    private String host;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl().and()
                .shutdownTimeout(Duration.ZERO)
                .build();

        RedisClusterConfiguration clusterConfiguration =
                new RedisClusterConfiguration(Collections.singleton(host));
        clusterConfiguration.setPassword(RedisPassword.of(password));

        return new LettuceConnectionFactory(clusterConfiguration, clientConfig);
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
