package com.mandiri.catalog.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ApiErrorResponse {
    String message;
}
