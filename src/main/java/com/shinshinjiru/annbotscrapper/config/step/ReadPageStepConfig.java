package com.shinshinjiru.annbotscrapper.config.step;

import com.shinshinjiru.annbotscrapper.batch.ReadPageTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Read Page step config.
 * ======================
 *
 * Configures the ReadPage step.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
public class ReadPageStepConfig {
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ApplicationContext context;

    @Bean("readPage")
    public Step readPage() {
        return steps.get("readPage")
                .tasklet(context.getBean(ReadPageTasklet.class))
                .build();
    }
}
