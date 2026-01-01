package com.csio.hexagonal.infrastructure.rest.router.group.contract;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.models.GroupedOpenApi;
import java.util.ArrayList;
import java.util.List;

public interface GroupedOpenApiProvider {

    default GroupedOpenApi createGroupedOpenApi(String groupName, String title, String pathPattern, String appVersion) {
        return GroupedOpenApi.builder()
                .group(groupName)
                .addOpenApiCustomizer(openApi ->
                        openApi.info(new Info().title(title).version(appVersion)))
                .addOperationCustomizer((operation, handlerMethod) -> {
                    // Create a list to hold the headers in the desired order
                    List<Parameter> headerParameters = new ArrayList<>();

                    // Add Bearer Token header first
                    headerParameters.add(new Parameter()
                            .name("Authorization")
                            .description("Bearer token for authentication")
                            .required(true)
                            .in("header")
                            .schema(new Schema<String>().type("string").example("Bearer <token>")));

                    // Add Language header next
                    headerParameters.add(new Parameter()
                            .name("Accept-Language")
                            .description("Language preference for the response")
                            .required(false)
                            .in("header")
                            .schema(new Schema<String>().type("string").example("en-US")));

                    // Add Currency header after Language
                    headerParameters.add(new Parameter()
                            .name("Currency")
                            .description("Currency preference for the transaction")
                            .required(false)
                            .in("header")
                            .schema(new Schema<String>().type("string").example("USD")));

                    // Add any existing parameters that might be present
                    if (operation.getParameters() != null) {
                        for (Parameter existingParameter : operation.getParameters()) {
                            // Avoid adding the header parameters again if they were already present
                            if (!existingParameter.getName().equals("Authorization") &&
                                !existingParameter.getName().equals("Accept-Language") &&
                                !existingParameter.getName().equals("Currency")) {
                                headerParameters.add(existingParameter);
                            }
                        }
                    }
                    // Set the parameters of the operation with the ordered list
                    operation.setParameters(headerParameters);

                    // Add default API responses
                    ApiResponses responses = new ApiResponses();
                    responses.addApiResponse("200", new ApiResponse().description("Operation successful"));
                    responses.addApiResponse("201", new ApiResponse().description("Resource created successfully"));
                    responses.addApiResponse("400", new ApiResponse().description("Bad request"));
                    responses.addApiResponse("404", new ApiResponse().description("Resource not found"));
                    responses.addApiResponse("500", new ApiResponse().description("Internal server error"));
                    operation.setResponses(responses);

                    return operation;
                })
                .pathsToMatch(pathPattern)
                .build();
    }
}