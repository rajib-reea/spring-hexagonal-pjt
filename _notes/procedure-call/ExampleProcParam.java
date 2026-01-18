package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import javax.persistence.ParameterMode;

/**
 * Per-procedure enum: declare all parameters for the stored procedure.
 * Keep one enum per procedure (or per logical group of procedures).
 *
 * name() is the actual database parameter name expected by the proc.
 */
public enum ExampleProcParam {
    IN_SEARCH("IN_SEARCH", ParameterMode.IN, String.class),
    IN_LIMIT("IN_LIMIT", ParameterMode.IN, Integer.class),
    OUT_STATUS("OUT_STATUS", ParameterMode.OUT, String.class),
    OUT_CURSOR("OUT_CURSOR", ParameterMode.REF_CURSOR, void.class); // REF_CURSOR type is provider-specific

    private final String paramName;
    private final ParameterMode mode;
    private final Class<?> type;

    ExampleProcParam(String paramName, ParameterMode mode, Class<?> type) {
        this.paramName = paramName;
        this.mode = mode;
        this.type = type;
    }

    public String name() {
        return paramName;
    }

    public ParameterMode mode() {
        return mode;
    }

    public Class<?> type() {
        return type;
    }
}
