package com.csio.hexagonal.infrastructure.rest.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for CORPIB dpr_src_act_info procedure")
public record DprSrcActInfoRequest(

        @NotBlank(message = "User code is required")
        @Size(max = 50, message = "User code must not exceed 50 characters")
        @Schema(description = "User code", example = "USER001")
        String userCode,

        @NotBlank(message = "Organization code is required")
        @Size(max = 50, message = "Organization code must not exceed 50 characters")
        @Schema(description = "Organization code", example = "ORG001")
        String orgCode,

        @NotBlank(message = "Account number is required")
        @Size(max = 50, message = "Account number must not exceed 50 characters")
        @Schema(description = "Account number", example = "1234567890")
        @JsonAlias("actNumber")
        String actNum
) {}
