package com.shinshinjiru.annbotscrapper.config.step;

import com.shinshinjiru.annbotscrapper.batch.PublishNewsTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publish News step config.
 * =========================
 * <p>
 * Configures the PublishNews step.
 *
 * @author manulaiko <manulaiko@gmail.com>
 */
@Configuration
public class PublishNewsStepConfig {
    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private ApplicationContext context;

    @Bean("publishNews")
    public Step readPage() {
        return steps.get("publishNews")
                .tasklet(context.getBean(PublishNewsTasklet.class))
                .build();
    }
}
