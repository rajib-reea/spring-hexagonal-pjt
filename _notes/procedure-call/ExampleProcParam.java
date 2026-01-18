package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import javax.persistence.ParameterMode;

/**
 * Example enum describing parameters for a stored procedure MY_SCHEMA.MY_PROCEDURE.
 * Create one enum per procedure or logical group of procedures.
 */
public enum ExampleProcParam implements StoredProcedureParam {
    IN_SEARCH("IN_SEARCH", ParameterMode.IN, String.class),
    IN_LIMIT("IN_LIMIT", ParameterMode.IN, Integer.class),
    OUT_STATUS("OUT_STATUS", ParameterMode.OUT, String.class),
    // If you want JPA/Hibernate to map REF_CURSOR rows to a DTO/entity, set the type here to that class.
    // Use Void.class if you will map rows manually from Object[].
    OUT_CURSOR("OUT_CURSOR", ParameterMode.REF_CURSOR, Void.class);

    private final String paramName;
    private final ParameterMode mode;
    private final Class<?> type;

    ExampleProcParam(String paramName, ParameterMode mode, Class<?> type) {
        this.paramName = paramName;
        this.mode = mode;
        this.type = type;
    }

    @Override
    public String name() {
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
