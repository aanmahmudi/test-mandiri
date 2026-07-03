package com.mandiri.catalog.api.controller;

import com.mandiri.catalog.api.dto.AdjustStockRequest;
import com.mandiri.catalog.api.dto.BookResponse;
import com.mandiri.catalog.api.dto.CreateBookRequest;
import com.mandiri.catalog.domain.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
        return bookService.create(request);
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable UUID id) {
        return bookService.get(id);
    }

    @GetMapping
    public List<BookResponse> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit
    ) {
        return bookService.list(q, limit);
    }

    @PatchMapping("/{id}/reserve")
    public BookResponse reserve(@PathVariable UUID id, @Valid @RequestBody AdjustStockRequest request) {
        return bookService.reserve(id, request.getQuantity());
    }

    @PatchMapping("/{id}/release")
    public BookResponse release(@PathVariable UUID id, @Valid @RequestBody AdjustStockRequest request) {
        return bookService.release(id, request.getQuantity());
    }
}
