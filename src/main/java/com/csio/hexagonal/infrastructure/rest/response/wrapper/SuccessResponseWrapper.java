package com.csio.hexagonal.infrastructure.rest.response.wrapper;

public record SuccessResponseWrapper<T>(
        int status,
        T data
) {}
