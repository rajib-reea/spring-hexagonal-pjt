package com.csio.hexagonal.infrastructure.store.persistence.procedure;

import jakarta.persistence.ParameterMode;

/**
 * Interface that enums should implement to describe a stored-procedure parameter.
 * Implement one enum per procedure (or per logical group).
 */
public interface StoredProcedureParam {
    /**
     * The parameter name as the stored procedure expects it (case and naming depends on DB).
     */
    String paramName();

    /**
     * The JPA ParameterMode (IN, OUT, INOUT, REF_CURSOR).
     */
    ParameterMode mode();

    /**
     * Java type of the parameter. For REF_CURSOR use Void.class if you will read result list,
     * or the entity/DTO class if you want JPA to map the result set to that class.
     */
    Class<?> type();
}
