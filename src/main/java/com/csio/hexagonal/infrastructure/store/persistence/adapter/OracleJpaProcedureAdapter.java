package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.OracleProcedurePort;
import com.csio.hexagonal.application.service.query.DprSrcActInfoQuery;
import com.csio.hexagonal.infrastructure.rest.response.corpib.DprSrcActInfoResponse;
import com.csio.hexagonal.infrastructure.store.persistence.procedure.AbstractStoredProcedureCaller;
import com.csio.hexagonal.infrastructure.store.persistence.procedure.AnotherProcParam;
import com.csio.hexagonal.infrastructure.store.persistence.procedure.DprSrcActInfoParam;
import com.csio.hexagonal.infrastructure.store.persistence.procedure.ExampleProcParam;
import com.csio.hexagonal.infrastructure.store.persistence.procedure.ParamValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Concrete adapter that extends AbstractStoredProcedureCaller and exposes multiple procedures.
 * Constructor expects a named EntityManager (procedureEntityManager).
 */
@Repository
public class OracleJpaProcedureAdapter extends AbstractStoredProcedureCaller implements OracleProcedurePort {

    public OracleJpaProcedureAdapter(@Qualifier("procedureEntityManager") EntityManager em) {
        super(em);
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "procedureTransactionManager")
    public DprSrcActInfoResponse dprSrcActInfo(DprSrcActInfoQuery query, String token) {
        Map<String, Object> outputs = executeProcedure(
                "CORPIB.dpr_src_act_info",
                DprSrcActInfoParam.values(),
                new ParamValue(DprSrcActInfoParam.IN_USER_CODE, normalizeIn(query.userCode())),
                new ParamValue(DprSrcActInfoParam.IN_ORG_CODE, normalizeIn(query.orgCode())),
                new ParamValue(DprSrcActInfoParam.IN_ACTNUM, normalizeIn(query.actNum()))
        );

        return mapDprSrcActInfo(outputs);
    }

    /**
     * Example: call MY_SCHEMA.MY_PROCEDURE using the ExampleProcParam enum.
     * Returns a map with OUT params and a key for the cursor (OUT_CURSOR).
     */
    @Transactional(readOnly = true, transactionManager = "procedureTransactionManager")
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
    @Transactional(readOnly = true, transactionManager = "procedureTransactionManager")
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

    private DprSrcActInfoResponse mapDprSrcActInfo(Map<String, Object> outputs) {
        String actType = asString(outputs.get(DprSrcActInfoParam.OUT_ACTTYPE.paramName()));
        String actName = asString(outputs.get(DprSrcActInfoParam.OUT_ACTNAME.paramName()));
        BigDecimal actBal = asBigDecimal(outputs.get(DprSrcActInfoParam.OUT_ACTBAL.paramName()));
        String status = asString(outputs.get(DprSrcActInfoParam.OUT_STATUS.paramName()));
        Integer code = asInteger(outputs.get(DprSrcActInfoParam.OUT_CODE.paramName()));
        String message = asString(outputs.get(DprSrcActInfoParam.OUT_MESSAGE.paramName()));

        return new DprSrcActInfoResponse(actType, actName, actBal, status, code, message);
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static BigDecimal asBigDecimal(Object value) {
        if (value instanceof BigDecimal bigDecimal) return bigDecimal;
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        return null;
    }

    private static Integer asInteger(Object value) {
        if (value instanceof Integer integer) return integer;
        if (value instanceof Number number) return number.intValue();
        return null;
    }

    private static String normalizeIn(String value) {
        return value == null ? "" : value;
    }
}
