package com.mandiri.catalog.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBookRequest {
    @NotBlank
    @Size(max = 32)
    private String isbn;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 150)
    private String author;

    @NotNull
    @Min(1450)
    @Max(2100)
    private Integer publishedYear;

    @NotNull
    @Min(0)
    private Integer stockTotal;
}
