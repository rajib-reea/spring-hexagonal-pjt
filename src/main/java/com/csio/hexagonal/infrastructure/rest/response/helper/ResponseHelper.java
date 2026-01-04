package com.csio.hexagonal.infrastructure.rest.response.helper;

import com.csio.hexagonal.infrastructure.rest.response.wrapper.SuccessResponseWrapper;

public class ResponseHelper {

    public static <T> SuccessResponseWrapper<T> success(T data) {
        return new SuccessResponseWrapper<>(200, data);
    }
}
