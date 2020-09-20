package com.shinshinjiru.annbotscrapper.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
public class BatchConfig extends DefaultBatchConfigurer {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    @Qualifier("readPage")
    private Step readPage;

    @Autowired
    @Qualifier("processPage")
    private Step processPage;

    @Autowired
    @Qualifier("filterNewsItems")
    private Step filterNewsItems;

    @Override
    public void setDataSource(DataSource dataSource) {
        //This BatchConfigurer ignores any DataSource
    }

    @Bean
    public Job job() {
        return jobs.get("annbot-scrapper")
                .start(readPage)
                .next(processPage)
                .next(filterNewsItems)
                .build();
    }
}
