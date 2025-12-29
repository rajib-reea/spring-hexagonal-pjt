package com.csio.hexagonal.domain.vo;

import java.util.Objects;

public record State(String value) {

    public State {
        Objects.requireNonNull(value, "State cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("State cannot be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
