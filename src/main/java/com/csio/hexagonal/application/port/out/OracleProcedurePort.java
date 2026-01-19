package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.application.service.query.DprSrcActInfoQuery;
import com.csio.hexagonal.infrastructure.rest.response.corpib.DprSrcActInfoResponse;

/**
 * Outbound port for Oracle stored procedure calls.
 */
public interface OracleProcedurePort {
    DprSrcActInfoResponse dprSrcActInfo(DprSrcActInfoQuery query, String token);
}
