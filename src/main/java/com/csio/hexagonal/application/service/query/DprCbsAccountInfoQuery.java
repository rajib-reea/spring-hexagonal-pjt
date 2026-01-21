package com.csio.hexagonal.application.service.query;

/**
 * Read-only query object for STLBAS.dpr_cbs_account_info.
 */
public record DprCbsAccountInfoQuery(
        String actNum
) {}
