package com.shinshinjiru.annbotscrapper.config;

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
}