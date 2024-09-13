package com.roman.import_sales_info.batch.processor;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import com.roman.import_sales_info.batch.mapper.SalesInfoMapper;
import com.roman.import_sales_info.domain.SalesInfo;
import org.springframework.batch.item.ItemProcessor;


public class SalesInfoItemProcessor implements ItemProcessor<SalesInfoDto, SalesInfo> {
    private SalesInfoMapper salesInfoMapper;
    @Override
    public SalesInfo process(SalesInfoDto item) throws Exception {
        return salesInfoMapper.INSTANCE.mapToEntity(item);
    }
}
