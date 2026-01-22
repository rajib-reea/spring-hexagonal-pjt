package com.csio.hexagonal.infrastructure.rest.response.wrapper;

import java.util.List;

public record PageResponseWrapper<T>(
        boolean success,
        int statusCode,
        Meta meta,
        List<T> data
) {

    public record Meta(
            int page,
            int size,
            long offset,
            long totalElements,
            int totalPages
    ) {}
}
