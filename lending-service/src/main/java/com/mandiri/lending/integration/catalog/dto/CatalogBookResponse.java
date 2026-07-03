package com.mandiri.lending.integration.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogBookResponse {
    private UUID id;
    private String isbn;
    private String title;
    private String author;
}
