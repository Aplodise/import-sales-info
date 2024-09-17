package com.roman.import_sales_info.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomStepExecutionListener implements StepExecutionListener {
    private final JdbcClient jdbcClient;
    private static final String DELETE_QUERY = "DELETE FROM sales_info WHERE id > 0";
    @Override
    public void beforeStep(StepExecution stepExecution) {
      int delRows = jdbcClient.sql(DELETE_QUERY).update();
      log.info("------> Before Step deleted rows: {}", delRows);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(ExitStatus.COMPLETED.equals(stepExecution.getExitStatus())){
            log.info("The step finished with success");
            return stepExecution.getExitStatus();
        }
        log.info("Something happened during the step, resulted in exit status: {}", stepExecution.getExitStatus());
        return stepExecution.getExitStatus();
    }
}
