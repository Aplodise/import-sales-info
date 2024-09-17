package com.roman.import_sales_info.batch.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class CustomJobExecutionListener implements JobExecutionListener {
    private static final String INPUT_FILE_NAME = "input.file.name";
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("---------------------> Before Job Execution");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("---------------------> After job computing the business logic");
        JobParameters jobParameters = jobExecution.getJobParameters();
        Map<String, JobParameter<?>> parameterMap = jobParameters.getParameters();
        if (parameterMap.containsKey(INPUT_FILE_NAME))
            compute(jobExecution, parameterMap);
    }

    private void compute(JobExecution jobExecution, Map<String, JobParameter<?>> parameterMap) {
        String absolutePath = (String) parameterMap.get(INPUT_FILE_NAME).getValue();
        Path absolutePathToFile = Path.of(absolutePath);
        Path sourceDirectory = absolutePathToFile.getParent();

        Path processedPath = Paths.get(sourceDirectory + File.separator + "processed");
        Path failedPath = Paths.get(sourceDirectory + File.separator + "failed");

        if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())){
            createDirectoryIfAbsent(processedPath);
            moveFile(absolutePathToFile, processedPath);
        }
        if(ExitStatus.FAILED.getExitCode().equals(jobExecution.getExitStatus().getExitCode()) ||
                ExitStatus.STOPPED.getExitCode().equals(jobExecution.getExitStatus().getExitCode())) {
            createDirectoryIfAbsent(failedPath);
            moveFile(absolutePathToFile, failedPath);
        }
    }

    @SneakyThrows
    void moveFile(final Path absolutePathToFile, final Path targetDirectory){
       Path destination = targetDirectory.resolve(absolutePathToFile.getFileName());
       Files.move(absolutePathToFile, destination, StandardCopyOption.ATOMIC_MOVE);
    }

    @SneakyThrows
    void createDirectoryIfAbsent(final Path directoryPath){
        Objects.requireNonNull(directoryPath, "Directory path cannot be null");
        if (Files.notExists(directoryPath)){
            Files.createDirectories(directoryPath);
        }
    }
}
