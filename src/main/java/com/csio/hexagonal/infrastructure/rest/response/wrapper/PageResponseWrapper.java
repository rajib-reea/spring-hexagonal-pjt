package com.csio.hexagonal.infrastructure.rest.response.wrapper;

import java.util.List;

public record PageResponseWrapper<T>(
        int status,
        List<T> content,
        int page,
        int size,
        long offset,
        long totalElements,
        int totalPages
) {}
