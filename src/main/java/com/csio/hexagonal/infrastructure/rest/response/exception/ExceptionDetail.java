package com.csio.hexagonal.infrastructure.rest.response.exception;

import java.time.Instant;

public record ExceptionDetail(
        String path,
        String error,
        String message,
        Instant timestamp
) {}
