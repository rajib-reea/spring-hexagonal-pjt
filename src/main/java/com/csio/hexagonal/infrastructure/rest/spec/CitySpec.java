//package com.csio.hexagonal.infrastructure.rest.spec;
//
//public class CitySpec{
//
//    public static final String ENTITY = "city";
//
//    // Summary Constants
//    public static final String CREATE_SUMMARY = "Create a new " + ENTITY;
//    public static final String GET_SUMMARY = "Get " + ENTITY + " by ID";
//    public static final String GET_ALL_SUMMARY = "Get all " + ENTITY + " by page and size";
//    public static final String UPDATE_SUMMARY = "Update " + ENTITY;
//    public static final String DELETE_SUMMARY = "Delete " + ENTITY + " by ID";
//
//    // Description Constants
//    public static final String CREATE_DESCRIPTION =
//            "Create a new " + ENTITY + " with the provided details";
//    public static final String GET_DESCRIPTION =
//            "Retrieve " + ENTITY + " details using its unique identifier";
//    public static final String GET_ALL_DESCRIPTION =
//            "Retrieve a paginated list of " + ENTITY + " entries";
//    public static final String UPDATE_DESCRIPTION =
//            "Update an existing " + ENTITY + " with the provided details";
//    public static final String DELETE_DESCRIPTION =
//            "Delete a " + ENTITY + " using its unique identifier";
//
//    // Parameter Constants
//    public static final String PARAMETER_DESCRIPTION =
//            "Unique identifier of the " + ENTITY;
//
//    // Example Constants
//    public static final String CREATE_EXAMPLE_NAME =
//            "Create City Example";
//
//    public static final String CREATE_EXAMPLE_DESCRIPTION =
//            "An example of a successful city creation request";
//
//    public static final String CREATE_EXAMPLE_VALUE = """
//        {
//            "name": "San Francisco",
//            "state": "California"
//        }
//        """;
//
//    public static final String UPDATE_EXAMPLE_VALUE = """
//        {
//            "name": "Los Angeles",
//            "state": "California"
//        }
//        """;
//}
package com.csio.hexagonal.infrastructure.rest.spec;

public class CitySpec {

    public static final String ENTITY = "city";

    // ================= Summary Constants =================
    public static final String CREATE_SUMMARY = "Create a new " + ENTITY;
    public static final String GET_SUMMARY = "Get " + ENTITY + " by ID";
    public static final String GET_ALL_SUMMARY = "Get all " + ENTITY + " by page and size";
    public static final String UPDATE_SUMMARY = "Update " + ENTITY;
    public static final String DELETE_SUMMARY = "Delete " + ENTITY + " by ID";

    // ================= Description Constants =================
    public static final String CREATE_DESCRIPTION =
            "Create a new " + ENTITY + " with the provided details";

    public static final String GET_DESCRIPTION =
            "Retrieve " + ENTITY + " details using its unique identifier";

    public static final String GET_ALL_DESCRIPTION =
            "Retrieve a paginated list of " + ENTITY + " entries with optional filtering, searching, and sorting";

    public static final String UPDATE_DESCRIPTION =
            "Update an existing " + ENTITY + " with the provided details";

    public static final String DELETE_DESCRIPTION =
            "Delete a " + ENTITY + " using its unique identifier";

    // ================= Parameter Constants =================
    public static final String PARAMETER_DESCRIPTION =
            "Unique identifier of the " + ENTITY;

    public static final String AUTH_HEADER_DESCRIPTION =
            "Bearer token for authentication (e.g., 'Authorization: Bearer <token>')";

    public static final String PAGE_PARAM_DESCRIPTION =
            "Page number (0-based index) for paginated results";

    public static final String SIZE_PARAM_DESCRIPTION =
            "Number of items per page for paginated results";

    public static final String SEARCH_PARAM_DESCRIPTION =
            "Filter cities by name or state; supports partial match";

    public static final String SORT_PARAM_DESCRIPTION =
            "Sorting criteria in the format 'field,direction' (e.g., 'name,asc' or 'state,desc')";

    // ================= Example Constants =================
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
    public static final String GET_ALL_EXAMPLE_NAME =
            "Get All City Example";
        public static final String GET_ALL_EXAMPLE_VALUE = """
        {
          "operator": "OR",
          "filterGroups": [
            {
              "operator": "AND",
              "conditions": [
                { "field": "state", "operator": "EQUALS", "value": "California" },
                { "field": "active", "operator": "EQUALS", "value": "true" }
              ]
            },
            {
              "operator": "OR",
              "conditions": [
                { "field": "name", "operator": "LIKE", "value": "San" },
                { "field": "name", "operator": "LIKE", "value": "Los" }
              ]
            }
          ],
          "page": 1,
          "size": 20,
          "sort": [
            { "field": "name", "direction": "ASC" },
            { "field": "state", "direction": "DESC" }
          ]
        }
        """;
}
