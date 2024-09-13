package com.roman.import_sales_info.batch;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import com.roman.import_sales_info.batch.processor.SalesInfoItemProcessor;
import com.roman.import_sales_info.domain.SalesInfo;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SalesInfoJobConfig {

    @Value("${input.csv.store}")
    private Resource resource;
    private final EntityManagerFactory entityManagerFactory;
    private final SalesInfoItemProcessor itemProcessor;

    public Step fromFileToDb(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("fromFileToDb", jobRepository)
                .<SalesInfoDto, SalesInfo>chunk(25, platformTransactionManager)
                .reader(salesInfoFileItemReader())
                .processor(itemProcessor)
                .writer(salesInfoJpaItemWriter())
                .build();
    }

    public FlatFileItemReader<SalesInfoDto> salesInfoFileItemReader(){
        return new FlatFileItemReaderBuilder<SalesInfoDto>()
                .name("sales info reader")
                .resource(resource)
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
}
