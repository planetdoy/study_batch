package com.doy.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * title : 4. Spring Batch 가이드 - Spring Batch Job Flow
 * url : <a href="https://jojoldu.tistory.com/328?category=902551">...</a>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * .on()
        * 캐치할 ExitStatus 지정
        * * 일 경우 모든 ExitStatus가 지정된다.
     * to()
        * 다음으로 이동할 Step 지정
     * from()
        * 일종의 이벤트 리스너 역할
        * 상태값을 보고 일치하는 상태라면 to()에 포함된 step을 호출합니다.
        * step1의 이벤트 캐치가 FAILED로 되있는 상태에서 추가로 이벤트 캐치하려면 from을 써야만 함
     * end()
        * end는 FlowBuilder를 반환하는 end와 FlowBuilder를 종료하는 end 2개가 있음
        * on("*")뒤에 있는 end는 FlowBuilder를 반환하는 end
        * build() 앞에 있는 end는 FlowBuilder를 종료하는 end
        * FlowBuilder를 반환하는 end 사용시 계속해서 from을 이어갈 수 있음
     *
     * 중요한 점은 on이 캐치하는 상태값이 BatchStatus가 아닌 ExitStatus라는 점입니다.
     * 그래서 분기처리를 위해 상태값 조정이 필요하시다면 ExitStatus를 조정해야합니다.
     */
    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("stepNextConditionalJob")
                .start(conditionalJobStep1())
                    .on("FAILED") // Failed 경우
                    .to(conditionalJobStep3()) // STEP3 으로 이동
                    .on("*") // step3 결과에 관계없이
                    .end() // step3으로 이동하면 flow 종료
                .from(conditionalJobStep1()) // step1로부터
                    .on("*") // failed 외 모든 경우
                    .to(conditionalJobStep2()) // step2로 이동
                    .next(conditionalJobStep3()) // step2 정상 종료 시 step3로 이동
                    .on("*") // step3의 결과 관계 없이
                    .end() // step3으로 이동하면 flow 종료
                .end() // job 종료
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>> this is stepNextConditionalJob Step1");

                    /*
                      ExitStatus를 FAILED로 지정한다.
                      해당 status를 보고 flow가 진행된다.
                     */
//                    contribution.setExitStatus(ExitStatus.FAILED);

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep2(){
        return stepBuilderFactory.get("conditionalJobStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>> this is stepNextConditionalJob Step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return stepBuilderFactory.get("conditionalJobStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(" >>> this is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    /**
     * StepExecutionListener 에서는 먼저 Step이 성공적으로 수행되었는지 확인하고,
     * StepExecution의 skip 횟수가 0보다 클 경우 COMPLETED WITH SKIPS 의 exitCode를 갖는 ExitStatus를 반환합니다.
     */
    public static class SkipCheckingListener extends StepExecutionListenerSupport {

        public ExitStatus afterStep(StepExecution stepExecution) {
            String exitCode = stepExecution.getExitStatus().getExitCode();
            if (!exitCode.equals(ExitStatus.FAILED.getExitCode()) &&
                    stepExecution.getSkipCount() > 0) {
                return new ExitStatus("COMPLETED WITH SKIPS");
            }
            else {
                return null;
            }
        }
    }

}
