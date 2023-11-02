package com.doy.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .next(simpleStep2())
                .build();
    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1") // "simpleStep1"란 이름의 Batch Step 생성
                .tasklet((contribution, chunkContext) -> { // Step 안에서 수행될 기능들을 명시
                    log.info(">>>>>> This is Step1");
                    return RepeatStatus.FINISHED;
//                    throw new IllegalArgumentException("step1 실패");
                })
                .build();
    }

    @Bean
    public Step simpleStep2() {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> This is Step2");

                    log.info(">>> requestData = {}", "nothing");

                    return RepeatStatus.FINISHED;
                }).build();
    }

}
