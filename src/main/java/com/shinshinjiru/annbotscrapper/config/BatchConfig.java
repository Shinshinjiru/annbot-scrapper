package com.shinshinjiru.annbotscrapper.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Batch configuration.
 * ====================
 *
 * Configures the batch tasklets.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    @Qualifier("readPage")
    private Step readPage;

    @Autowired
    @Qualifier("processPage")
    private Step processPage;

    @Bean
    public Job job() {
        return jobs.get("annbot-scrapper")
                .start(readPage)
                .next(processPage)
                .build();
    }
}
