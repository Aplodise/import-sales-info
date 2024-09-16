package com.roman.import_sales_info.batch.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.time.Duration;

@Configuration
@EnableIntegration
@IntegrationComponentScan
@RequiredArgsConstructor
public class SalesInfoIntegrationConfig {

    @Value("${sales.info.dir}")
    private String salesDir;
    private final Job job;
    private final JobRepository jobRepository;

    @Bean
    public IntegrationFlow integrationFlow(){
        return IntegrationFlow.from(fileReadingMessageSource(),
                sourcePolling -> sourcePolling.poller(Pollers.fixedDelay(Duration.ofSeconds(5)).maxMessagesPerPoll(1)))
                .channel(fileIn())
                .handle(fileRenameProcessHandler())
                .transform(fileMessageToJobRequest())
                .handle(jobLaunchingGateway())
                .log()
                .get();
    }

    public FileReadingMessageSource fileReadingMessageSource(){
        var messageSource = new FileReadingMessageSource();
        messageSource.setDirectory(new File(salesDir));
        messageSource.setFilter(new SimplePatternFileListFilter("*.csv"));
        return messageSource;
    }
    public DirectChannel fileIn(){
        return new DirectChannel();
    }

    public MessageHandler fileRenameProcessHandler(){
     var fileWritingMessage = new FileWritingMessageHandler(new File(salesDir));
     fileWritingMessage.setFileExistsMode(FileExistsMode.REPLACE);
     fileWritingMessage.setDeleteSourceFiles(Boolean.TRUE);
     fileWritingMessage.setRequiresReply(Boolean.FALSE);
     fileWritingMessage.setFileNameGenerator(fileNameGenerator());
     return fileWritingMessage;
    }

    public DefaultFileNameGenerator fileNameGenerator(){
        var fileNameGenerator = new DefaultFileNameGenerator();
        fileNameGenerator.setExpression("payload.name + '.processing'");
        return fileNameGenerator;
    }

    public FileMessageToJobRequest fileMessageToJobRequest(){
        var transformer = new FileMessageToJobRequest();
        transformer.setJob(job);
       return transformer;
    }

    public JobLaunchingGateway jobLaunchingGateway(){
        var taskExecutorJobLauncher = new TaskExecutorJobLauncher();
        taskExecutorJobLauncher.setJobRepository(jobRepository);
        taskExecutorJobLauncher.setTaskExecutor(new SyncTaskExecutor());
        return new JobLaunchingGateway(taskExecutorJobLauncher);
    }
}
