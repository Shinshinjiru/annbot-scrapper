package com.shinshinjiru.annbotscrapper.config;

import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

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

    @Value("${anilist.authToken}")
    private String authToken;

    /**
     * Configures the Lettuce driver.
     *
     * @return Lettuce connection factory.
     */
    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> {
            if (clientConfigurationBuilder.build().isUseSsl()) {
                clientConfigurationBuilder.useSsl().disablePeerVerification();
            }
        };
    }

    /**
     * Configures the Redis template.
     *
     * @return Redis template.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        return template;
    }

    @Bean
    public ApolloClient apolloClient() {
        return ApolloClient.builder()
                .serverUrl(anilistApi)
                .okHttpClient(new OkHttpClient.Builder()
                        .addInterceptor((c) -> {
                            var request = c.request().newBuilder()
                                    .addHeader("Authorization", "Bearer " + authToken)
                                    .build();

                            return c.proceed(request);
                        })
                        .build()
                )
                .build();
    }
}
