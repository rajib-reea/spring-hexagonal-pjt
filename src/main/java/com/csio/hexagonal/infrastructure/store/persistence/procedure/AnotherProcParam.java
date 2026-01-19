package com.csio.hexagonal.infrastructure.store.persistence.procedure;

import jakarta.persistence.ParameterMode;

/**
 * Example second-procedure enum. Replace with actual parameters for the second stored procedure.
 */
public enum AnotherProcParam implements StoredProcedureParam {
    IN_ID("IN_ID", ParameterMode.IN, Long.class),
    OUT_MESSAGE("OUT_MESSAGE", ParameterMode.OUT, String.class);

    private final String paramName;
    private final ParameterMode mode;
    private final Class<?> type;

    AnotherProcParam(String paramName, ParameterMode mode, Class<?> type) {
        this.paramName = paramName;
        this.mode = mode;
        this.type = type;
    }

    @Override
    public String paramName() {
        return paramName;
    }

    @Override
    public ParameterMode mode() {
        return mode;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
