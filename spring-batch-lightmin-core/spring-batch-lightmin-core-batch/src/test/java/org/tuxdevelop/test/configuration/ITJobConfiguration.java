package org.tuxdevelop.test.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.tuxdevelop.spring.batch.lightmin.batch.annotation.EnableLightminBatch;
import org.tuxdevelop.spring.batch.lightmin.repository.annotation.EnableLightminJdbcConfigurationRepository;

@Slf4j
@Configuration
@EnableLightminBatch
@EnableLightminJdbcConfigurationRepository
@RequiredArgsConstructor
public class ITJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job simpleJob() {
        return new JobBuilder("simpleJob", jobRepository)
                .start(this.simpleStep())
                .build();
    }

    @Bean
    public Step simpleStep() {
        return new StepBuilder("simpleStep", jobRepository)
                .<Long, Long>chunk(1, transactionManager)
                .reader(new SimpleReader())
                .writer(new SimpleWriter())
                .allowStartIfComplete(Boolean.TRUE)
                .build();
    }

    public static class SimpleReader implements ItemReader<Long> {

        private static final Long[] values = {1L, 2L, 3L, 4L};
        private int index = 0;

        @Override
        public Long read() throws Exception {
            final Long value = this.index >= values.length ? null : values[this.index];
            this.index++;
            return value;
        }

    }

    public static class SimpleWriter implements ItemWriter<Long> {
        @Override
        public void write(Chunk<? extends Long> chunk) throws Exception {
            for (final Long value : chunk) {
                log.info(String.valueOf(value));
            }
        }
    }

}
