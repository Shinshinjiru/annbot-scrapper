package com.shinshinjiru.annbotscrapper.batch;

import com.shinshinjiru.annbotscrapper.api.Client;
import com.shinshinjiru.annbotscrapper.model.NewsItem;
import com.shinshinjiru.annbotscrapper.repository.NewsItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Publish News tasklet.
 * =====================
 * <p>
 * Publishes the news in the rabbitMQ queue and updates redis.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Slf4j
@Component
public class PublishNewsTasklet implements Tasklet, StepExecutionListener {
    @Autowired
    private NewsItemRepository repository;

    @Autowired
    private Client client;

    private List<NewsItem> result;

    /**
     * Initialize the state of the listener with the {@link StepExecution} from
     * the current scope.
     *
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("PublishNewsTasklet init");

        //noinspection unchecked
        result = (ArrayList<NewsItem>) stepExecution.getJobExecution().getExecutionContext().get("news");
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
        log.info("PublishNewsTasklet end");

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
        if (result.size() == 0) {
            log.info("No new items.");

            return RepeatStatus.FINISHED;
        }

        log.info("Publishing items...");
        client.publish(result);

        log.info("Updating redis...");
        repository.deleteAll();
        repository.save(result.get(0));

        return RepeatStatus.FINISHED;
    }
}
