package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.infrastructure.rest.response.SuccessResponseWrapper;

public class ResponseMapper {

    public static <T> SuccessResponseWrapper<T> success(T data) {
        return new SuccessResponseWrapper<>(200, data);
    }
}
