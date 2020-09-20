package com.shinshinjiru.annbotscrapper.batch;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filter NewsItems tasklet.
 * =========================
 * <p>
 * Filters out all the unpublished news up until the specified max in config `ann.maxItems`.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Slf4j
@Component
public class FilterNewsItemsTasklet implements Tasklet, StepExecutionListener {
    @Value("${ann.maxItems}")
    private int max;

    @Autowired
    private NewsItemRepository repository;

    private List<NewsItem> result;

    /**
     * Initialize the state of the listener with the {@link StepExecution} from
     * the current scope.
     *
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("FilterNewsItemsTasklet init");

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
        log.info("FilterNewsItemsTasklet end");

        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("news", result);

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
        log.info("Retrieving last item from redis...");
        var lastItems = (ArrayList<NewsItem>)repository.findAll();

        // Build takeWhile predicate.
        // Default to take all.
        Predicate<NewsItem> takeWhile = newsItem -> true;
        if (lastItems.size() > 0) {
            var last = lastItems.get(lastItems.size() - 1);
            // Take only until last item id.
            takeWhile = newsItem -> newsItem.getId() != last.getId();
        }

        log.info("Filtering up to "+ max +" items...");
        // Take all items until last item.
        var resultsUntil = result.stream()
                .takeWhile(takeWhile)
                .collect(Collectors.toList());

        // Return last MAX items.
        result = resultsUntil.stream()
                .skip(Math.max(0, resultsUntil.size() - max))
                .collect(Collectors.toList());

        return RepeatStatus.FINISHED;
    }
}
