package com.shinshinjiru.annbotscrapper.batch;

import com.shinshinjiru.annbotscrapper.model.NewsItem;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

/**
 * Process Page tasklet.
 * =====================
 * <p>
 * Processes the homepage and builds the news instances.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Slf4j
public class ProcessPageTasklet implements Tasklet, StepExecutionListener {
    @Value("${ann.url}")
    private String url;

    private Elements news;
    private ArrayList<NewsItem> result;

    /**
     * Initialize the state of the listener with the {@link StepExecution} from
     * the current scope.
     *
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("ProcessPageTasklet init");

        //noinspection ConstantConditions
        news = ((Document) stepExecution.getExecutionContext().get("homepage"))
                .select(".herald.box.news");

        result = new ArrayList<>(news.size());
    }

    /**
     * Give a listener a chance to modify the exit status from a step. The value
     * returned will be combined with the normal exit status using
     * {@link ExitStatus#and(ExitStatus)}.
     * <p>
     * Called after execution of step's processing logic (both successful or
     * failed). Throwing exception in this method has no effect, it will only be
     * logged.
     *
     * @param stepExecution {@link StepExecution} instance.
     * @return an {@link ExitStatus} to combine with the normal value. Return
     * {@code null} to leave the old value unchanged.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("ProcessPageTasklet end");

        stepExecution.getExecutionContext().put("news", result);

        return ExitStatus.COMPLETED;
    }

    /**
     * Given the current context in the form of a step contribution, do whatever
     * is necessary to process this unit inside a transaction. Implementations
     * return {@link RepeatStatus#FINISHED} if finished. If not they return
     * {@link RepeatStatus#CONTINUABLE}. On failure throws an exception.
     *
     * @param contribution mutable state to be passed back to update the current
     *                     step execution
     * @param chunkContext attributes shared between invocations but not between
     *                     restarts
     * @return an {@link RepeatStatus} indicating whether processing is
     * continuable. Returning {@code null} is interpreted as {@link RepeatStatus#FINISHED}
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        news.forEach(n -> {
            // <div class="thumbnail" data-src="{THUMBNAIL}"></div>
            var thumbnail = n.select(".thumbnail")
                    .attr("data-src");

            // <div class="wrap"><a>{TITLE}</a></div>
            var title = n.select(".wrap a")
                    .text()
                    .trim();

            // <div class="preview"><div class="intro">{INTRO}</div><div class="full">{FULL}</div></div>
            var preview = n.select(".wrap .preview")
                    .text()
                    .trim();

            // <div class="comments"><a href="/cms/discuss/{ID}">comments</a></div>
            var id = n.select(".wrap .byline .comments a")
                    .attr("href")
                    .split("/")[2];

            var item =  NewsItem.builder()
                            .thumbnail(url + thumbnail)
                            .title(url + title)
                            .preview(preview)
                            .id(Integer.parseInt(id))
                            .build();

            log.debug(item.toString());

            result.add(item);
        });

        return RepeatStatus.FINISHED;
    }
}
