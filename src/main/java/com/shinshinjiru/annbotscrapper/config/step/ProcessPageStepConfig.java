package com.shinshinjiru.annbotscrapper.config.step;

import com.shinshinjiru.annbotscrapper.batch.ProcessPageTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Process Page step config.
 * =========================
 * <p>
 * Configures the ProcessPage step.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
public class ProcessPageStepConfig {
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ApplicationContext context;

    @Bean("processPage")
    public Step readPage() {
        return steps.get("processPage")
                .tasklet(context.getBean(ProcessPageTasklet.class))
                .build();
    }
}
