package com.csio.hexagonal.integration;

import com.csio.hexagonal.CityServiceApplication;
import com.csio.hexagonal.infrastructure.rest.request.CityCreateRequest;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.store.persistence.adapter.CityRepository;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for the complete City service.
 * Tests the entire flow from REST API through application layer to database persistence.
 */
@SpringBootTest(
        classes = CityServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class CityServiceE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CityRepository cityRepository;

    private static final String CITY_BASE_PATH = "/api/v1/city";
    private static final String AUTH_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        cityRepository.deleteAll();
    }

    @Test
    void shouldCreateCityAndPersistToDatabase() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("San Antonio", "TX");

        // Act - Create city via REST API
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();

        // Assert - Verify city exists in database
        List<CityEntity> cities = cityRepository.findAll();
        assertEquals(1, cities.size());
        assertEquals("San Antonio", cities.get(0).getName());
        assertEquals("TX", cities.get(0).getState());
        assertTrue(cities.get(0).getIsActive());
    }

    @Test
    void shouldEnforceDomainRulesAcrossAllLayers() {
        // Arrange - Create a city first
        CityCreateRequest request = new CityCreateRequest("Columbus", "OH");
        
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();

        // Act & Assert - Try to create duplicate (should be rejected by domain policy)
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isBadRequest();

        // Verify only one city exists in database
        List<CityEntity> cities = cityRepository.findAll();
        assertEquals(1, cities.size());
    }

    @Test
    void shouldRetrieveCityFromDatabaseViaApi() {
        // Arrange - Create city directly in database
        CityEntity entity = new CityEntity();
        entity.setName("Indianapolis");
        entity.setState("IN");
        entity.setIsActive(true);
        CityEntity saved = cityRepository.save(entity);

        // Act - Retrieve via API
        webTestClient.get()
                .uri(CITY_BASE_PATH + "/" + saved.getUid())
                .header("Authorization", AUTH_TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("success")
                .jsonPath("$.data.name").isEqualTo("Indianapolis")
                .jsonPath("$.data.state").isEqualTo("IN");
    }

    @Test
    void shouldSearchAndFilterCitiesAcrossAllLayers() {
        // Arrange - Create multiple cities in database
        createAndSaveCityEntity("Jacksonville", "FL");
        createAndSaveCityEntity("Fort Worth", "TX");
        createAndSaveCityEntity("Charlotte", "NC");

        // Act - Search via API
        CityFindAllRequest request = new CityFindAllRequest(
                null,
                1,
                10,
                "Fort",
                null
        );

        // Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("success")
                .jsonPath("$.meta.totalElements").isEqualTo(1)
                .jsonPath("$.data[0].name").isEqualTo("Fort Worth");
    }

    @Test
    void shouldApplyPaginationCorrectly() {
        // Arrange - Create 25 cities
        for (int i = 1; i <= 25; i++) {
            createAndSaveCityEntity("City " + String.format("%02d", i), "ST" + i);
        }

        // Act - Get first page
        CityFindAllRequest request1 = new CityFindAllRequest(null, 1, 10, null, null);
        
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request1))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.meta.page").isEqualTo(1)
                .jsonPath("$.meta.size").isEqualTo(10)
                .jsonPath("$.meta.totalElements").isEqualTo(25)
                .jsonPath("$.meta.totalPages").isEqualTo(3)
                .jsonPath("$.data.length()").isEqualTo(10);

        // Act - Get second page
        CityFindAllRequest request2 = new CityFindAllRequest(null, 2, 10, null, null);
        
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request2))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.meta.page").isEqualTo(2)
                .jsonPath("$.data.length()").isEqualTo(10);

        // Act - Get last page
        CityFindAllRequest request3 = new CityFindAllRequest(null, 3, 10, null, null);
        
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request3))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.meta.page").isEqualTo(3)
                .jsonPath("$.data.length()").isEqualTo(5);
    }

    @Test
    void shouldApplySortingCorrectly() {
        // Arrange
        createAndSaveCityEntity("Zebra City", "ZZ");
        createAndSaveCityEntity("Alpha City", "AA");
        createAndSaveCityEntity("Beta City", "BB");

        // Act - Sort ascending
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

        // Assert
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].name").isEqualTo("Alpha City")
                .jsonPath("$.data[1].name").isEqualTo("Beta City")
                .jsonPath("$.data[2].name").isEqualTo("Zebra City");
    }

    @Test
    void shouldApplyComplexFiltersCorrectly() {
        // Arrange
        createAndSaveCityEntity("Nashville", "TN");
        createAndSaveCityEntity("Memphis", "TN");
        createAndSaveCityEntity("Detroit", "MI");

        // Create filter for TN state
        CityFindAllRequest.FilterCondition condition = new CityFindAllRequest.FilterCondition(
                "state",
                CityFindAllRequest.Operator.EQUALS,
                "TN"
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
                .expectBody()
                .jsonPath("$.meta.totalElements").isEqualTo(2)
                .jsonPath("$.data[0].state").isEqualTo("TN")
                .jsonPath("$.data[1].state").isEqualTo("TN");
    }

    @Test
    void shouldValidateInputAtRestLayer() {
        // Test empty name
        CityCreateRequest invalidRequest1 = new CityCreateRequest("", "CA");
        
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidRequest1))
                .exchange()
                .expectStatus().isBadRequest();

        // Test empty state
        CityCreateRequest invalidRequest2 = new CityCreateRequest("San Jose", "");
        
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(invalidRequest2))
                .exchange()
                .expectStatus().isBadRequest();

        // Verify no cities were created
        List<CityEntity> cities = cityRepository.findAll();
        assertEquals(0, cities.size());
    }

    @Test
    void shouldHandleAuthorizationToken() {
        // Arrange
        CityCreateRequest request = new CityCreateRequest("El Paso", "TX");

        // Act & Assert - Request with token should succeed
        webTestClient.post()
                .uri(CITY_BASE_PATH)
                .header("Authorization", "valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk();

        // Note: The application currently accepts any token value
        // This test documents the current behavior
    }

    @Test
    void shouldMaintainDataConsistencyUnderLoad() {
        // Arrange - Create multiple cities concurrently (simulated)
        int numberOfCities = 10;
        
        for (int i = 1; i <= numberOfCities; i++) {
            CityCreateRequest request = new CityCreateRequest("City " + i, "ST" + i);
            
            webTestClient.post()
                    .uri(CITY_BASE_PATH)
                    .header("Authorization", AUTH_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(request))
                    .exchange()
                    .expectStatus().isOk();
        }

        // Act - Retrieve all cities
        CityFindAllRequest request = new CityFindAllRequest(null, 1, 20, null, null);
        
        // Assert - All cities should be persisted
        webTestClient.post()
                .uri(CITY_BASE_PATH + "/all")
                .header("Authorization", AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.meta.totalElements").isEqualTo(numberOfCities);

        // Verify database consistency
        List<CityEntity> cities = cityRepository.findAll();
        assertEquals(numberOfCities, cities.size());
    }

    // Helper method to create and save city entity
    private void createAndSaveCityEntity(String name, String state) {
        CityEntity entity = new CityEntity();
        entity.setName(name);
        entity.setState(state);
        entity.setIsActive(true);
        cityRepository.save(entity);
    }
}
