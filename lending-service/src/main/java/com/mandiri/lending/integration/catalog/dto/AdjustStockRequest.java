package com.mandiri.lending.integration.catalog.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdjustStockRequest {
    Integer quantity;
}
