package com.shinshinjiru.annbotscrapper.config;

import com.apollographql.apollo.ApolloClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

/**
 * Configuration class.
 * ====================
 * <p>
 * Configures the Spring application.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
@Slf4j
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
                        .addInterceptor((c) -> {
                            var request = c.request();

                            try {
                                final Request copy = request.newBuilder().build();
                                final Buffer buffer = new Buffer();
                                copy.body().writeTo(buffer);

                                log.debug("REQUEST");
                                log.debug(buffer.readUtf8());
                            } catch (final IOException e) {
                                log.warn("Failed to stringify request body: " + e.getMessage());
                            }

                            var response = c.proceed(request);
                            var responseBody = response.body();
                            if (responseBody == null) {
                                return response;
                            }

                            var responseBodyString = responseBody.string();

                            response = response.newBuilder()
                                    .body(
                                            ResponseBody.create(
                                                    responseBody.contentType(),
                                                    responseBodyString
                                            )
                                    )
                                    .build();

                            log.debug("RESPONSE");
                            log.debug(responseBodyString);

                            return response;
                        })
                        .build()
                )
                .build();
    }
}
