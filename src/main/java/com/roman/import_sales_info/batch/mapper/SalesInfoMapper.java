package com.roman.import_sales_info.batch.mapper;

import com.roman.import_sales_info.batch.dto.SalesInfoDto;
import com.roman.import_sales_info.domain.SalesInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SalesInfoMapper {
    SalesInfoMapper INSTANCE = Mappers.getMapper(SalesInfoMapper.class);

    SalesInfo mapToEntity(SalesInfoDto salesInfoDto);
}
