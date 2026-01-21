package com.csio.hexagonal.infrastructure.store.persistence.procedure;

import jakarta.persistence.ParameterMode;
import java.math.BigDecimal;

/**
 * Parameter metadata for STLBAS.dpr_cbs_account_info.
 */
public enum DprCbsAccountInfoParam implements StoredProcedureParam {
    IN_ACTNUM("IN_ACTNUM", ParameterMode.IN, String.class),
    OUT_BRANCD("OUT_BRANCD", ParameterMode.OUT, String.class),
    OUT_ACTYPE("OUT_ACTYPE", ParameterMode.OUT, String.class),
    OUT_ACTTIT("OUT_ACTTIT", ParameterMode.OUT, String.class),
    OUT_CURBAL("OUT_CURBAL", ParameterMode.OUT, BigDecimal.class),
    OUT_CODE("OUT_CODE", ParameterMode.OUT, String.class),
    OUT_MESSAGE("OUT_MESSAGE", ParameterMode.OUT, String.class);

    private final String paramName;
    private final ParameterMode mode;
    private final Class<?> type;

    DprCbsAccountInfoParam(String paramName, ParameterMode mode, Class<?> type) {
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
