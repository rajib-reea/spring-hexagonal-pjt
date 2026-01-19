package com.csio.hexagonal.infrastructure.store.persistence.procedure;

import jakarta.persistence.ParameterMode;
import java.math.BigDecimal;

/**
 * Parameter metadata for CORPIB.dpr_src_act_info.
 */
public enum DprSrcActInfoParam implements StoredProcedureParam {
    IN_USER_CODE("IN_USER_CODE", ParameterMode.IN, String.class),
    IN_ORG_CODE("IN_ORG_CODE", ParameterMode.IN, String.class),
    IN_ACTNUM("IN_ACTNUM", ParameterMode.IN, String.class),
    OUT_ACTTYPE("OUT_ACTTYPE", ParameterMode.OUT, String.class),
    OUT_ACTNAME("OUT_ACTNAME", ParameterMode.OUT, String.class),
    OUT_ACTBAL("OUT_ACTBAL", ParameterMode.OUT, BigDecimal.class),
    OUT_STATUS("OUT_STATUS", ParameterMode.OUT, String.class),
    OUT_CODE("OUT_CODE", ParameterMode.OUT, Integer.class),
    OUT_MESSAGE("OUT_MESSAGE", ParameterMode.OUT, String.class);

    private final String paramName;
    private final ParameterMode mode;
    private final Class<?> type;

    DprSrcActInfoParam(String paramName, ParameterMode mode, Class<?> type) {
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
