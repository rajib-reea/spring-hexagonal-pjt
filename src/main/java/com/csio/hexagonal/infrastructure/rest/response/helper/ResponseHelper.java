package com.csio.hexagonal.infrastructure.rest.response.helper;

import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.SuccessResponseWrapper;
import org.springframework.data.domain.Page;

public class ResponseHelper {

    public static <T> PageResponseWrapper<T> page(Page<T> pageResult) {
        PageResponseWrapper.Meta meta = new PageResponseWrapper.Meta(
                pageResult.getNumber() + 1,  // convert 0-based to 1-based
                pageResult.getSize(),
                pageResult.getPageable().getOffset(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return new PageResponseWrapper<>(
                true,
                200,
                meta,
                pageResult.getContent()
        );
    }

    public static <T> SuccessResponseWrapper<T> success(T data) {
        return new SuccessResponseWrapper<>(true, 200, data);
    }
}
