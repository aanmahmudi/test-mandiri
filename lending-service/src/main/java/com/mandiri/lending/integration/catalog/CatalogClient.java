package com.mandiri.lending.integration.catalog;

import com.mandiri.lending.common.NotFoundException;
import com.mandiri.lending.integration.catalog.dto.AdjustStockRequest;
import com.mandiri.lending.integration.catalog.dto.CatalogBookResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class CatalogClient {
    private final RestClient restClient;

    public CatalogClient(RestClient catalogRestClient) {
        this.restClient = catalogRestClient;
    }

    public CatalogBookResponse getBook(UUID bookId) {
        try {
            return restClient.get()
                    .uri("/api/books/{id}", bookId)
                    .retrieve()
                    .body(CatalogBookResponse.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("book not found in catalog");
            }
            throw ex;
        }
    }

    public void reserve(UUID bookId, int quantity) {
        try {
            restClient.patch()
                    .uri("/api/books/{id}/reserve", bookId)
                    .body(AdjustStockRequest.builder().quantity(quantity).build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("book not found in catalog");
            }
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new IllegalStateException("insufficient stock");
            }
            throw ex;
        }
    }

    public void release(UUID bookId, int quantity) {
        try {
            restClient.patch()
                    .uri("/api/books/{id}/release", bookId)
                    .body(AdjustStockRequest.builder().quantity(quantity).build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("book not found in catalog");
            }
            throw ex;
        }
    }
}
