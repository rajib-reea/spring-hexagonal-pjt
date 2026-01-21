package com.csio.hexagonal.infrastructure.rest.response.corpib;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DprCbsAccountInfoResponse(
        String brancd,
        String actype,
        String acttit,
        BigDecimal curbal,
        String code,
        String message
) {}
