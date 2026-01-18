package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.OracleProcedurePort;
import com.csio.hexagonal.infrastructure.store.persistence.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JPA-based adapter that calls a stored procedure using createStoredProcedureQuery.
 * Use the enum + record approach to register parameters and pass IN values.
 *
 * Notes about REF_CURSOR:
 * - How REF_CURSOR is returned is provider-specific (Hibernate supports ParameterMode.REF_CURSOR).
 * - getResultList() may return a List<Object[]> or mapped entities depending on registration.
 */
@Repository
public class OracleJpaProcedureAdapter implements OracleProcedurePort {

    private static final Logger log = LoggerFactory.getLogger(OracleJpaProcedureAdapter.class);
    private final EntityManager em;

    public OracleJpaProcedureAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> callExampleProcedure(ParamValue... params) {
        try {
            // Fully-qualified proc name (schema.PROC_NAME) or just PROC_NAME depending on DB user/owner
            String procName = "MY_SCHEMA.MY_PROCEDURE";

            StoredProcedureQuery query = em.createStoredProcedureQuery(procName);

            // Register all parameters defined in the enum
            for (ExampleProcParam p : ExampleProcParam.values()) {
                // registerStoredProcedureParameter(name, type, mode)
                query.registerStoredProcedureParameter(p.name(), p.type(), p.mode());
            }

            // Set IN / INOUT parameters from provided ParamValue records
            if (params != null) {
                for (ParamValue pv : params) {
                    ExampleProcParam p = pv.param();
                    if (p.mode() == javax.persistence.ParameterMode.IN
                            || p.mode() == javax.persistence.ParameterMode.INOUT) {
                        query.setParameter(p.name(), pv.value());
                    }
                }
            }

            // Execute
            boolean hasResultSet = query.execute();

            Map<String, Object> outputs = new HashMap<>();

            // Collect OUT / INOUT / REF_CURSOR results
            for (ExampleProcParam p : ExampleProcParam.values()) {
                switch (p.mode()) {
                    case OUT:
                    case INOUT:
                        Object outVal = query.getOutputParameterValue(p.name());
                        outputs.put(p.name(), outVal);
                        break;
                    case REF_CURSOR:
                        // Try to obtain result list from the REF_CURSOR
                        // Some providers return a result list via getResultList(); others via getOutputParameterValue
                        Object cursorResult;
                        try {
                            // If stored procedure produced a result set, getResultList() yields it
                            cursorResult = query.getResultList();
                        } catch (IllegalStateException ise) {
                            // Fallback: some drivers expose REF_CURSOR as an OUT parameter
                            cursorResult = query.getOutputParameterValue(p.name());
                        }
                        outputs.put(p.name(), cursorResult);
                        break;
                    default:
                        // no-op for IN-only params
                        break;
                }
            }

            log.debug("Stored procedure {} outputs: {}", procName, outputs);
            return outputs;
        } catch (PersistenceException ex) {
            log.error("Error calling stored procedure", ex);
            throw new DatabaseException("Failed to call stored procedure", ex);
        }
    }
}
