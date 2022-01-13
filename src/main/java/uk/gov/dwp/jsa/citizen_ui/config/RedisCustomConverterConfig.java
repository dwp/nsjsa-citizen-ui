package uk.gov.dwp.jsa.citizen_ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.convert.RedisCustomConversions;

import java.util.Arrays;

@Configuration
public class RedisCustomConverterConfig {

    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(
                Arrays.asList(
                        new UuidToByteArrayConverter(),
                        new ByteArrayToUuidConverter()
                )
        );
    }

}
