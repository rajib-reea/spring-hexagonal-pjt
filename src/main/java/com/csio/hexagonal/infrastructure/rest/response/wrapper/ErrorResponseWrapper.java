package com.csio.hexagonal.infrastructure.rest.response.wrapper;

import com.csio.hexagonal.infrastructure.rest.exception.ExceptionDetail;

public record ErrorResponseWrapper(
        int status,
        ExceptionDetail exception
) {}
