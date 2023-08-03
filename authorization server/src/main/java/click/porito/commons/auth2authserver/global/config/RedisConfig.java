package click.porito.commons.auth2authserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * redis configuration
 */
@Configuration
public class RedisConfig {

    @Profile("test")
    @Configuration
    static class UnSharedMemorySessionConfig {
        //use local memory session
    }

    @Profile({"prod","local"})
    @Configuration
    @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10)
    static class SharedSessionConfig {
        @Bean
        public LettuceConnectionFactory connectionFactory() {
            return new LettuceConnectionFactory();
        }
    }

}
