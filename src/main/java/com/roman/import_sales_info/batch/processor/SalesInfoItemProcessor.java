package com.roman.import_sales_info.batch.processor;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import com.roman.import_sales_info.batch.mapper.SalesInfoMapper;
import com.roman.import_sales_info.domain.SalesInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesInfoItemProcessor implements ItemProcessor<SalesInfoDto, SalesInfo> {
    private SalesInfoMapper salesInfoMapper;
    @Override
    public SalesInfo process(SalesInfoDto item) throws Exception {
        log.info("Processing the item: {}", item);
        return salesInfoMapper.INSTANCE.mapToEntity(item);
    }
}
