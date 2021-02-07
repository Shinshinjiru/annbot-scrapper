package com.shinshinjiru.annbotscrapper.config;

import com.apollographql.apollo.ApolloClient;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Configuration class.
 * ====================
 * <p>
 * Configures the Spring application.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
public class ApplicationConfig {
    @Value("${anilist.api}")
    private String anilistApi;

    /**
     * Configures the Jedis driver.
     *
     * @return Jedis connection factory.
     */
    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        var config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);

        return new JedisConnectionFactory(config);
    }

    /**
     * Configures the Redis template.
     *
     * @return Redis template.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());

        return template;
    }

    /**
     * Configures the RabbitMQ queue.
     *
     * @return RabbitMQ queue.
     */
    @Bean
    public Queue queue() {
        return new Queue("shinshinjiru", false);
    }

    @Bean
    public ApolloClient apolloClient() {
        return ApolloClient.builder()
                .serverUrl(anilistApi)
                .build();
    }
}
