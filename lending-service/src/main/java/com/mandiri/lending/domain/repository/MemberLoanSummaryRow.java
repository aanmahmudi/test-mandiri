package com.mandiri.lending.domain.repository;

import java.util.UUID;

public interface MemberLoanSummaryRow {
    UUID getMemberId();

    String getMemberName();

    Long getActiveCount();

    Long getTotalCount();
}
