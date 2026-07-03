package com.mandiri.lending.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookSnapshotResponse {
    UUID id;
    String isbn;
    String title;
    String author;
}
