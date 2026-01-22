package com.csio.hexagonal.infrastructure.rest.response.wrapper;

public record SuccessResponseWrapper<T>(
        boolean success,
        int statusCode,
        T data
) {}
