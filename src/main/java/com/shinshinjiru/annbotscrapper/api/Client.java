package com.shinshinjiru.annbotscrapper.api;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.shinshinjiru.annbotscrapper.model.NewsItem;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * API client.
 * ===========
 * <p>
 * Client for the AniList API.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Service
@Slf4j
public class Client {
    @Autowired
    private ApolloClient client;

    @Value("${ann.url}")
    private String url;

    @Value("classpath:activity.tpl")
    private Resource template;

    /**
     * Publishes the given news.
     *
     * @param news News to publish.
     */
    public void publish(List<NewsItem> news) {
        try (var reader = new InputStreamReader(template.getInputStream(), StandardCharsets.UTF_8)) {
            var tpl = FileCopyUtils.copyToString(reader);

            var msg = new StringBuilder();

            news.forEach(i -> {
                var link = url + i.getId();

                msg.append(
                        tpl.replace("${LINK}", link)
                                .replace("${THUMBNAIL}", i.getThumbnail())
                                .replace("${TITLE}", i.getTitle())
                                .replace("${PREVIEW}", i.getPreview())
                );
            });

            var future = new CompletableFuture<Boolean>();
            client.mutate(SaveTextActivityMutation.builder()
                    .text(msg.toString())
                    .build()
            )
                    .enqueue(new ApolloCall.Callback<>() {
                        @Override
                        public void onResponse(@NotNull Response<SaveTextActivityMutation.Data> response) {
                            log.info("News published to https://anilist.co/activity/" + response.getData().SaveTextActivity.id);

                            future.complete(true);
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            log.warn("Couldn't publish news", e);

                            future.completeExceptionally(e);
                        }
                    });

            // Block thread until the request is finished
            Mono.fromFuture(future).block();
        } catch (Exception e) {
            log.error("Couldn't publish news", e);
        }
    }
}
