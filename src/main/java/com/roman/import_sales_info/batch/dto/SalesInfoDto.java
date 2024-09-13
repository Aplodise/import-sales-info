package com.roman.import_sales_info.batch.dto;


public record SalesInfoDto(String product,
                           String seller,
                           Long sellerId,
                           Double price,
                           String city,
                           String category) {
}
