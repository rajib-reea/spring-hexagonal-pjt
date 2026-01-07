package com.csio.hexagonal.infrastructure.rest.response.helper;

import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.SuccessResponseWrapper;
import org.springframework.data.domain.Page;

public class ResponseHelper {
    public static <T> PageResponseWrapper<T> page(Page<T> pageResult) {
        return new PageResponseWrapper<>(
                200,
                pageResult.getContent(),
                pageResult.getNumber() + 1,  // convert 0-based to 1-based for clients
                pageResult.getSize(),
                pageResult.getPageable().getOffset(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public static <T> SuccessResponseWrapper<T> success(T data) {
        return new SuccessResponseWrapper<>(200, data);
    }
}
