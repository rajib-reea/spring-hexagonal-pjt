package com.csio.hexagonal.application.service.query;

/**
 * Read-only query object for CORPIB.dpr_src_act_info.
 */
public record DprSrcActInfoQuery(
        String userCode,
        String orgCode,
        String actNum
) {}
