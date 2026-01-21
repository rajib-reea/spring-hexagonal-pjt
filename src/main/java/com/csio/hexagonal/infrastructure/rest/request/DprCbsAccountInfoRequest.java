package com.csio.hexagonal.infrastructure.rest.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for STLBAS.dpr_cbs_account_info procedure")
public record DprCbsAccountInfoRequest(

        @NotBlank(message = "Account number is required")
        @Size(max = 50, message = "Account number must not exceed 50 characters")
        @Schema(description = "Account number", example = "08533000197")
        @JsonAlias("actNumber")
        String actNum
) {}
