package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.OracleProcedurePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * Concrete adapter that extends AbstractStoredProcedureCaller and exposes multiple procedures.
 * Constructor expects a named EntityManager (oracleEntityManager) if you created a dedicated persistence unit.
 */
@Repository
public class OracleJpaProcedureAdapter extends AbstractStoredProcedureCaller implements OracleProcedurePort {

    public OracleJpaProcedureAdapter(@Qualifier("oracleEntityManager") EntityManager em) {
        super(em);
    }

    /**
     * Example: call MY_SCHEMA.MY_PROCEDURE using the ExampleProcParam enum.
     * Returns a map with OUT params and a key for the cursor (OUT_CURSOR).
     */
    @Override
    @Transactional(readOnly = true, transactionManager = "oracleTransactionManager")
    public Map<String, Object> callExampleProcedure(ParamValue... params) {
        return executeProcedure(
                "MY_SCHEMA.MY_PROCEDURE",
                ExampleProcParam.values(),
                params
        );
    }

    /**
     * Another example: call a different procedure with its own enum.
     * Define AnotherProcParam enum implementing StoredProcedureParam similarly.
     */
    @Transactional(readOnly = true, transactionManager = "oracleTransactionManager")
    public Map<String, Object> callAnotherProcedure(ParamValue... params) {
        return executeProcedure(
                "MY_SCHEMA.ANOTHER_PROC",
                AnotherProcParam.values(),
                params
        );
    }

    /**
     * Convenience helper to fetch a list from a REF_CURSOR result and map Object[] rows to DTOs if necessary.
     * If OUT_CURSOR was defined with Void.class, you'll often receive List<Object[]>.
     * The calling code can inspect the returned Map and convert results accordingly.
     */
    @SuppressWarnings("unchecked")
    public List<?> extractCursor(Map<String, Object> outputs, String cursorParamName) {
        Object cursor = outputs.get(cursorParamName);
        if (cursor instanceof List<?> list) return list;
        return List.of();
    }
}
