package com.csio.hexagonal.infrastructure.rest.response;

public class SuccessResponseWrapper<T> {

    private final int status;
    private final T data;

    public SuccessResponseWrapper(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
