package com.mandiri.lending.domain.service;

import com.mandiri.lending.api.dto.MemberLoanSummaryResponse;
import com.mandiri.lending.domain.repository.LoanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {
    private final LoanRepository loanRepository;

    public AnalyticsService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberLoanSummaryResponse> memberLoanSummary(long minActive) {
        return loanRepository.findMemberLoanSummary(minActive).stream()
                .map(r -> MemberLoanSummaryResponse.builder()
                        .memberId(r.getMemberId())
                        .memberName(r.getMemberName())
                        .activeCount(r.getActiveCount())
                        .totalCount(r.getTotalCount())
                        .build())
                .toList();
    }
}
