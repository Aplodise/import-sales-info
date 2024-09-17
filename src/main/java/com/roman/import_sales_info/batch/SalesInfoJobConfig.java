package com.roman.import_sales_info.batch;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import com.roman.import_sales_info.batch.faulttolerance.CustomSkipPolicy;
import com.roman.import_sales_info.batch.processor.SalesInfoItemProcessor;
import com.roman.import_sales_info.domain.SalesInfo;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SalesInfoJobConfig {


    private final EntityManagerFactory entityManagerFactory;
    private final SalesInfoItemProcessor itemProcessor;
    private final CustomSkipPolicy customSkipPolicy;
    @Bean
    public Job importSalesInfo(JobRepository jobRepository, Step fromFileToDb){
        return new JobBuilder("importSalesInfo", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fromFileToDb)
                .build();
    }

    @Bean
    public Step fromFileToDb(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, ItemReader<SalesInfoDto> salesInfoDtoItemReader){
        return new StepBuilder("fromFileToDb", jobRepository)
                .<SalesInfoDto, SalesInfo>chunk(100, platformTransactionManager)
                .taskExecutor(taskExecutor())
                .reader(salesInfoDtoItemReader)
                .processor(itemProcessor)
                .writer(salesInfoJpaItemWriter())
                .faultTolerant()
                .skipPolicy(customSkipPolicy)
                .build();
    }
    @Bean
    @StepScope
    public FlatFileItemReader<SalesInfoDto> salesInfoFileItemReader(@Value("#{jobParameters['input.file.name']}") String resource){
        return new FlatFileItemReaderBuilder<SalesInfoDto>()
                .resource(new FileSystemResource(resource))
                .name("sales info reader")
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("product", "seller", "sellerId", "price", "city", "category")
                .targetType(SalesInfoDto.class)
                .build();
    }

    public JpaItemWriter<SalesInfo> salesInfoJpaItemWriter(){
        return new JpaItemWriterBuilder<SalesInfo>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    public TaskExecutor taskExecutor(){
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Thread N -> ");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    public AsyncItemProcessor<SalesInfoDto, SalesInfo> asyncItemProcessor(){
        var asyncItemProcessor = new AsyncItemProcessor<SalesInfoDto, SalesInfo>();
        asyncItemProcessor.setDelegate(itemProcessor);
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        return asyncItemProcessor;
    }
    public AsyncItemWriter<SalesInfo> asyncItemWriter(){
        var asyncItemWriter = new AsyncItemWriter<SalesInfo>();
        asyncItemWriter.setDelegate(salesInfoJpaItemWriter());
        return asyncItemWriter;
    }
}
