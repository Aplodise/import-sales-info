package com.roman.import_sales_info.batch.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;

import java.io.File;

@Configuration
@EnableIntegration
@IntegrationComponentScan
@RequiredArgsConstructor
public class SalesInfoIntegrationConfig {

    @Value("${sales.info.dir}")
    private String salesDir;

    public FileReadingMessageSource fileReadingMessageSource(){
        var messageSource = new FileReadingMessageSource();
        messageSource.setDirectory(new File(salesDir));
        messageSource.setFilter(new SimplePatternFileListFilter("*.csv"));
        return messageSource;
    }
}
