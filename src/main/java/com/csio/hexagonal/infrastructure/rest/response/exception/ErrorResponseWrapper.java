package com.csio.hexagonal.infrastructure.rest.response.exception;

public record ErrorResponseWrapper(
        int status,
        ExceptionDetail exception
) {}
