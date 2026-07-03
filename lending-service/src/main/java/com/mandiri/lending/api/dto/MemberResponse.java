package com.mandiri.lending.api.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberResponse {
    UUID id;
    String name;
    String email;
    Instant createdAt;
    Instant updatedAt;
}
