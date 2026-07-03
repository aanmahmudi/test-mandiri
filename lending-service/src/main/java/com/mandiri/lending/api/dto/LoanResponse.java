package com.mandiri.lending.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoanResponse {
    UUID id;
    UUID memberId;
    UUID bookId;
    String status;
    LocalDate dueDate;
    Instant returnedAt;
    Instant createdAt;
    Instant updatedAt;
    BookSnapshotResponse book;
}
