package com.csio.hexagonal.infrastructure.rest.spec;

public class CitySpec{

    public static final String ENTITY = "city";

    // Summary Constants
    public static final String CREATE_SUMMARY = "Create a new " + ENTITY;
    public static final String GET_SUMMARY = "Get " + ENTITY + " by ID";
    public static final String GET_ALL_SUMMARY = "Get all " + ENTITY + " by page and size";
    public static final String UPDATE_SUMMARY = "Update " + ENTITY;
    public static final String DELETE_SUMMARY = "Delete " + ENTITY + " by ID";

    // Description Constants
    public static final String CREATE_DESCRIPTION =
            "Create a new " + ENTITY + " with the provided details";
    public static final String GET_DESCRIPTION =
            "Retrieve " + ENTITY + " details using its unique identifier";
    public static final String GET_ALL_DESCRIPTION =
            "Retrieve a paginated list of " + ENTITY + " entries";
    public static final String UPDATE_DESCRIPTION =
            "Update an existing " + ENTITY + " with the provided details";
    public static final String DELETE_DESCRIPTION =
            "Delete a " + ENTITY + " using its unique identifier";

    // Parameter Constants
    public static final String PARAMETER_DESCRIPTION =
            "Unique identifier of the " + ENTITY;

    // Example Constants
    public static final String CREATE_EXAMPLE_NAME =
            "Create City Example";

    public static final String CREATE_EXAMPLE_DESCRIPTION =
            "An example of a successful city creation request";

    public static final String CREATE_EXAMPLE_VALUE = """
        {
            "name": "San Francisco",
            "state": "California"
        }
        """;

    public static final String UPDATE_EXAMPLE_VALUE = """
        {
            "name": "Los Angeles",
            "state": "California"
        }
        """;
}
