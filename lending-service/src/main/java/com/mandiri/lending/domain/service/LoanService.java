package com.mandiri.lending.domain.service;

import com.mandiri.lending.api.dto.BookSnapshotResponse;
import com.mandiri.lending.api.dto.CreateLoanRequest;
import com.mandiri.lending.api.dto.LoanResponse;
import com.mandiri.lending.common.BadRequestException;
import com.mandiri.lending.common.NotFoundException;
import com.mandiri.lending.domain.entity.Loan;
import com.mandiri.lending.domain.entity.LoanStatus;
import com.mandiri.lending.domain.entity.Member;
import com.mandiri.lending.domain.repository.LoanRepository;
import com.mandiri.lending.integration.catalog.CatalogClient;
import com.mandiri.lending.integration.catalog.dto.CatalogBookResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final CatalogClient catalogClient;

    public LoanService(LoanRepository loanRepository, MemberService memberService, CatalogClient catalogClient) {
        this.loanRepository = loanRepository;
        this.memberService = memberService;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public LoanResponse create(CreateLoanRequest request) {
        Member member = memberService.getEntity(request.getMemberId());

        if (loanRepository.existsByMember_IdAndBookIdAndStatus(member.getId(), request.getBookId(), LoanStatus.ACTIVE)) {
            throw new BadRequestException("member already has an active loan for this book");
        }

        catalogClient.reserve(request.getBookId(), 1);

        int days = request.getDays() == null ? 14 : request.getDays();
        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .member(member)
                .bookId(request.getBookId())
                .status(LoanStatus.ACTIVE)
                .dueDate(LocalDate.now().plusDays(days))
                .build();

        Loan saved = loanRepository.save(loan);
        return toResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public LoanResponse get(UUID id, boolean includeBook) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new NotFoundException("loan not found"));
        return toResponse(loan, includeBook);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> list(UUID memberId, LoanStatus status) {
        List<Loan> loans = status == null
                ? loanRepository.findByMember_Id(memberId)
                : loanRepository.findByMember_IdAndStatus(memberId, status);

        return loans.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(l -> toResponse(l, false))
                .toList();
    }

    @Transactional
    public LoanResponse markReturned(UUID loanId) {
        Loan loan = loanRepository.findByIdForUpdate(loanId);
        if (loan == null) {
            throw new NotFoundException("loan not found");
        }
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BadRequestException("loan already returned");
        }

        loan.markReturned(Instant.now());
        Loan saved = loanRepository.save(loan);
        catalogClient.release(saved.getBookId(), 1);
        return toResponse(saved, true);
    }

    private LoanResponse toResponse(Loan loan, boolean includeBook) {
        BookSnapshotResponse book = null;
        if (includeBook) {
            try {
                CatalogBookResponse b = catalogClient.getBook(loan.getBookId());
                book = BookSnapshotResponse.builder()
                        .id(b.getId())
                        .isbn(b.getIsbn())
                        .title(b.getTitle())
                        .author(b.getAuthor())
                        .build();
            } catch (RuntimeException ignored) {
                book = null;
            }
        }

        return LoanResponse.builder()
                .id(loan.getId())
                .memberId(loan.getMember().getId())
                .bookId(loan.getBookId())
                .status(loan.getStatus().name())
                .dueDate(loan.getDueDate())
                .returnedAt(loan.getReturnedAt())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .book(book)
                .build();
    }
}
