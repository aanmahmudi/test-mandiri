package com.mandiri.catalog.domain.service;

import com.mandiri.catalog.api.dto.BookResponse;
import com.mandiri.catalog.api.dto.CreateBookRequest;
import com.mandiri.catalog.common.BadRequestException;
import com.mandiri.catalog.common.NotFoundException;
import com.mandiri.catalog.domain.entity.Book;
import com.mandiri.catalog.domain.repository.BookRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse create(CreateBookRequest request) {
        bookRepository.findByIsbn(request.getIsbn()).ifPresent(b -> {
            throw new BadRequestException("isbn already exists");
        });

        UUID id = UUID.randomUUID();
        Book book = Book.builder()
                .id(id)
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publishedYear(request.getPublishedYear())
                .stockTotal(request.getStockTotal())
                .stockAvailable(request.getStockTotal())
                .build();

        Book saved = bookRepository.saveAndFlush(book);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookResponse get(UUID id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("book not found"));
        return toResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> list(String q, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<Book> books = (q == null || q.isBlank()) ? bookRepository.findAll() : bookRepository.search(q, safeLimit);
        return books.stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookResponse reserve(UUID id, int quantity) {
        Book book = bookRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("book not found"));
        book.reserve(quantity);
        return toResponse(bookRepository.saveAndFlush(book));
    }

    @Transactional
    public BookResponse release(UUID id, int quantity) {
        Book book = bookRepository.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("book not found"));
        book.release(quantity);
        return toResponse(bookRepository.saveAndFlush(book));
    }

    private BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publishedYear(book.getPublishedYear())
                .stockTotal(book.getStockTotal())
                .stockAvailable(book.getStockAvailable())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
