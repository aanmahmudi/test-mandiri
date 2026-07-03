package com.mandiri.lending.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateLoanRequest {
    @NotNull
    private UUID memberId;

    @NotNull
    private UUID bookId;

    @Min(1)
    @Max(60)
    private Integer days;
}
