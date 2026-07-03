package com.mandiri.catalog.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdjustStockRequest {
    @NotNull
    @Min(1)
    private Integer quantity;
}
