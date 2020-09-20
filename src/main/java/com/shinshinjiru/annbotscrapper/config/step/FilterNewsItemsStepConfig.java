package com.shinshinjiru.annbotscrapper.config.step;

import com.shinshinjiru.annbotscrapper.batch.FilterNewsItemsTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter NewsItems step config.
 * =============================
 * <p>
 * Configures the FilterNewsItems step.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
public class FilterNewsItemsStepConfig {
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ApplicationContext context;

    @Bean("filterNewsItems")
    public Step readPage() {
        return steps.get("filterNewsItems")
                .tasklet(context.getBean(FilterNewsItemsTasklet.class))
                .build();
    }
}
