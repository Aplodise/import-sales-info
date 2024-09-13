package com.roman.import_sales_info.batch;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SalesInfoJobConfig {

    @Value("${input.csv.store}")
    private Resource resource;

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
}
