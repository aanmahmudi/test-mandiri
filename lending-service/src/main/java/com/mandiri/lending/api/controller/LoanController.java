package com.mandiri.lending.api.controller;

import com.mandiri.lending.api.dto.CreateLoanRequest;
import com.mandiri.lending.api.dto.LoanResponse;
import com.mandiri.lending.domain.entity.LoanStatus;
import com.mandiri.lending.domain.service.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public LoanResponse create(@Valid @RequestBody CreateLoanRequest request) {
        return loanService.create(request);
    }

    @PostMapping("/{id}/return")
    public LoanResponse markReturned(@PathVariable UUID id) {
        return loanService.markReturned(id);
    }

    @GetMapping("/{id}")
    public LoanResponse get(
            @PathVariable UUID id,
            @RequestParam(value = "include_book", required = false, defaultValue = "false") boolean includeBook
    ) {
        return loanService.get(id, includeBook);
    }

    @GetMapping
    public List<LoanResponse> list(
            @RequestParam("member_id") UUID memberId,
            @RequestParam(value = "status", required = false) String status
    ) {
        LoanStatus parsedStatus = status == null ? null : LoanStatus.valueOf(status.toUpperCase());
        return loanService.list(memberId, parsedStatus);
    }
}
