package com.mandiri.lending.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberLoanSummaryResponse {
    UUID memberId;
    String memberName;
    Long activeCount;
    Long totalCount;
}
