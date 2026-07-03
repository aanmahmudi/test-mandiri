package com.mandiri.catalog.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookResponse {
    UUID id;
    String isbn;
    String title;
    String author;
    Integer publishedYear;
    Integer stockTotal;
    Integer stockAvailable;
    Instant createdAt;
    Instant updatedAt;
}
