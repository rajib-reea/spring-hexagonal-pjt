package com.csio.hexagonal.domain.vo;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements, int totalPages) {
        return new PageResult<>(content, page, size, totalElements, totalPages);
    }
}
