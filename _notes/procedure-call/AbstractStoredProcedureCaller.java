package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.infrastructure.store.persistence.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

/**
 * Generic parent class that performs stored-procedure calls using JPA EntityManager.
 * Subclasses can call executeProcedure(...) for different stored procedures.
 *
 * This class:
 * - Registers parameters based on StoredProcedureParam[] metadata
 * - Accepts ParamValue... for IN/INOUT inputs
 * - Returns a Map<String,Object> containing OUT/INOUT parameter values and REF_CURSOR results
 *
 * Notes:
 * - For REF_CURSOR mapping: if a REF_CURSOR param's type() != Void.class, this class will try to
 *   create the StoredProcedureQuery with the result class so JPA/Hibernate maps rows to that class.
 * - Behavior for REF_CURSOR is provider-specific.
 */
public abstract class AbstractStoredProcedureCaller {

    private static final Logger log = LoggerFactory.getLogger(AbstractStoredProcedureCaller.class);

    protected final EntityManager em;

    protected AbstractStoredProcedureCaller(EntityManager em) {
        this.em = em;
    }

    /**
     * Execute a stored procedure.
     *
     * @param procName  fully qualified procedure name (schema.proc or proc depending on DB user)
     * @param paramDefs array of StoredProcedureParam constants describing parameters for this proc
     * @param paramVals runtime values for IN/INOUT parameters
     * @return map of parameter name -> value (OUT, INOUT, REF_CURSOR results)
     */
    protected Map<String, Object> executeProcedure(String procName,
                                                   StoredProcedureParam[] paramDefs,
                                                   ParamValue... paramVals) {
        try {
            // If any REF_CURSOR param specifies a mapping class, use the first as resultClass
            Class<?> resultClass = findFirstRefCursorResultClass(paramDefs);

            StoredProcedureQuery query = (resultClass != null)
                    ? em.createStoredProcedureQuery(procName, resultClass)
                    : em.createStoredProcedureQuery(procName);

            // Register params
            for (StoredProcedureParam p : paramDefs) {
                // Some providers expect Void.class for REF_CURSOR registration; we forward the provided type
                query.registerStoredProcedureParameter(p.name(), p.type(), p.mode());
            }

            // Map param name -> value for quick lookup
            Map<String, Object> inValues = new HashMap<>();
            if (paramVals != null) {
                for (ParamValue pv : paramVals) {
                    inValues.put(pv.param().name(), pv.value());
                }
            }

            // Set IN / INOUT params
            for (StoredProcedureParam p : paramDefs) {
                if (p.mode() == ParameterMode.IN || p.mode() == ParameterMode.INOUT) {
                    if (inValues.containsKey(p.name())) {
                        query.setParameter(p.name(), inValues.get(p.name()));
                    }
                }
            }

            boolean producedResultSet = query.execute();

            Map<String, Object> outputs = new LinkedHashMap<>();

            for (StoredProcedureParam p : paramDefs) {
                switch (p.mode()) {
                    case OUT, INOUT -> {
                        Object outVal = query.getOutputParameterValue(p.name());
                        outputs.put(p.name(), outVal);
                    }
                    case REF_CURSOR -> {
                        Object cursorResult;
                        try {
                            // Preferred: getResultList when the provider maps the REF_CURSOR
                            cursorResult = query.getResultList();
                        } catch (IllegalStateException ise) {
                            // Fallback to OUT parameter if provided that way
                            cursorResult = query.getOutputParameterValue(p.name());
                        }
                        outputs.put(p.name(), cursorResult);
                    }
                    default -> {
                        // IN-only: nothing to add
                    }
                }
            }

            log.debug("Procedure {} executed. outputs={}", procName, outputs);
            return outputs;
        } catch (PersistenceException ex) {
            log.error("Error executing stored procedure " + procName, ex);
            throw new DatabaseException("Failed to execute stored procedure: " + procName, ex);
        }
    }

    private Class<?> findFirstRefCursorResultClass(StoredProcedureParam[] paramDefs) {
        for (StoredProcedureParam p : paramDefs) {
            if (p.mode() == ParameterMode.REF_CURSOR && p.type() != Void.class) {
                return p.type();
            }
        }
        return null;
    }
}
