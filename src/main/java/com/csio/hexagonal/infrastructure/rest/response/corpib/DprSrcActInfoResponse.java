package com.csio.hexagonal.infrastructure.rest.response.corpib;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DprSrcActInfoResponse(
        String actType,
        String actName,
        BigDecimal actBal,
        String status,
        Integer code,
        String message
) {}
