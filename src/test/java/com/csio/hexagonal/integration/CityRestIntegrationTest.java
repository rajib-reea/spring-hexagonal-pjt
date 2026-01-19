package com.csio.hexagonal.integration;

import com.csio.hexagonal.CityServiceApplication;
import com.csio.hexagonal.infrastructure.rest.request.CityCreateRequest;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.SuccessResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for City REST API endpoints.
 * Tests the complete flow from HTTP request through all layers to the database.
 */
@SpringBootTest(
        classes = CityServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class CityRestIntegrationTest {

    @Autowired
    private ApplicationContext context;
    
    private WebTestClient webTestClient;

    private static final String CITY_BASE_PATH = "/api/v1/city";
    private static final String AUTH_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        // Manually configure WebTestClient
        this.webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void shouldCreateCitySuccessfully() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("New York", "NY");

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SuccessResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertNotNull(response.data());
                });
    }

    @Test
    void shouldRejectCityWithBlankName() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("", "CA");

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldRejectCityWithBlankState() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("Los Angeles", "");

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldRejectDuplicateCity() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("Chicago", "IL");

        // Act - Create first city
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();

        // Act - Try to create duplicate
        // Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldGetCityById() {
        // Arrange - Create a city first
        CityCreateRequest createRequest = new CityCreateRequest("Boston", "MA");
        
        SuccessResponseWrapper<CityResponse> createResponse = webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SuccessResponseWrapper.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createResponse);
        assertNotNull(createResponse.data());

        // Extract the UUID from the created city
        // Note: This assumes the response contains an id field
        String cityId = extractCityId(createResponse);

        // Act & Assert - Get the city by ID
        webTestClient.get()
                .uri(CITY_BASE_PATH + "/" + cityId)
                .header("Authorization", AUTH_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SuccessResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertNotNull(response.data());
                });
    }

    @Test
    void shouldReturnNotFoundForInvalidCityId() {
        // Arrange
        String invalidId = UUID.randomUUID().toString();

        // Act & Assert
        webTestClient.get()
                .uri(CITY_BASE_PATH + "/" + invalidId)
                .header("Authorization", AUTH_TOKEN)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void shouldGetAllCitiesWithPagination() {
        // Arrange - Create multiple cities
        createCity("Seattle", "WA");
        createCity("Portland", "OR");
        createCity("Denver", "CO");

        CityFindAllRequest request = new CityFindAllRequest(
                null,  // filter
                1,     // page
                10,    // size
                null,  // search
                null   // sort
        );

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertNotNull(response.data());
                    assertNotNull(response.meta());
                    assertTrue(response.meta().totalElements() >= 3);
                });
    }

    @Test
    void shouldSearchCitiesByName() {
        // Arrange - Create test cities
        createCity("San Francisco", "CA");
        createCity("San Diego", "CA");
        createCity("Austin", "TX");

        CityFindAllRequest request = new CityFindAllRequest(
                null,  // filter
                1,     // page
                10,    // size
                "San", // search
                null   // sort
        );

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertTrue(response.meta().totalElements() >= 2);
                });
    }

    @Test
    void shouldFilterCitiesByState() {
        // Arrange - Create test cities
        createCity("Miami", "FL");
        createCity("Tampa", "FL");
        createCity("Phoenix", "AZ");

        // Create filter for FL state
        CityFindAllRequest.FilterCondition condition = new CityFindAllRequest.FilterCondition(
                "state",
                CityFindAllRequest.Operator.EQUALS,
                "FL"
        );

        CityFindAllRequest.FilterGroup filterGroup = new CityFindAllRequest.FilterGroup(
                CityFindAllRequest.LogicalOperator.AND,
                List.of(condition)
        );

        CityFindAllRequest.Filter filter = new CityFindAllRequest.Filter(
                CityFindAllRequest.LogicalOperator.AND,
                List.of(filterGroup)
        );

        CityFindAllRequest request = new CityFindAllRequest(
                filter,
                1,
                10,
                null,
                null
        );

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertTrue(response.meta().totalElements() >= 2);
                });
    }

    @Test
    void shouldSortCitiesByName() {
        // Arrange - Create test cities
        createCity("Zebra City", "ZZ");
        createCity("Alpha City", "AA");
        createCity("Beta City", "BB");

        CityFindAllRequest.SortOrder sortOrder = new CityFindAllRequest.SortOrder(
                "name",
                CityFindAllRequest.Direction.ASC
        );

        CityFindAllRequest request = new CityFindAllRequest(
                null,
                1,
                10,
                null,
                List.of(sortOrder)
        );

        // Act & Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageResponseWrapper.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals("success", response.status());
                    assertNotNull(response.data());
                });
    }

    // Helper method to create a city
    private void createCity(String name, String state) {
        CityCreateRequest request = new CityCreateRequest(name, state);
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();
    }

    // Helper method to extract city ID from response
    // TODO: Improve ID extraction using JsonPath or proper response deserialization
    // Current implementation uses a workaround approach
    private String extractCityId(SuccessResponseWrapper<?> response) {
        Object data = response.data();
        if (data instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) data;
            Object id = map.get("id");
            if (id != null) {
                return id.toString();
            }
        }
        // Fallback: If extraction fails, use a placeholder
        // Note: This makes the test less reliable and should be improved
        return UUID.randomUUID().toString();
    }
}
