package com.roman.import_sales_info.batch.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendEmail implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("------------------> Sending email on COMPLETED WITH SKIPS");
        long readSkipCount = contribution.getReadSkipCount();
        log.info("----------> Job completed but was skipped: {} lines", readSkipCount);
        return RepeatStatus.FINISHED;
    }
}
