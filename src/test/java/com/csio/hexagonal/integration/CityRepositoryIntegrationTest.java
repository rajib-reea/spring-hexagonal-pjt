package com.csio.hexagonal.integration;

import com.csio.hexagonal.CityServiceApplication;
import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.store.persistence.adapter.CityRepository;
import com.csio.hexagonal.infrastructure.store.persistence.adapter.CityRepositoryAdapter;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for persistence layer using real H2 database.
 * Tests the adapter and repository with Spring Data JPA.
 */
@SpringBootTest(classes = CityServiceApplication.class)
@ActiveProfiles("test")
class CityRepositoryIntegrationTest {

    @Autowired
    private CityRepository repository;

    private CityRepositoryAdapter adapter;

    private static final String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        adapter = new CityRepositoryAdapter(repository);
        repository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveCity() {
        // Arrange
        City city = new City(CityId.newId(), "New York", new State("NY"));

        // Act
        City savedCity = adapter.save(city, TEST_TOKEN);

        // Assert
        assertNotNull(savedCity);
        assertNotNull(savedCity.getId());
        assertEquals("New York", savedCity.getName());
        assertEquals(new State("NY"), savedCity.getState());
        assertTrue(savedCity.isActive());
    }

    @Test
    void shouldFindCityByUid() {
        // Arrange
        City city = new City(CityId.newId(), "Los Angeles", new State("CA"));
        City savedCity = adapter.save(city, TEST_TOKEN);
        UUID savedId = savedCity.getId().value();

        // Act
        Optional<City> foundCity = adapter.findByUid(savedId, TEST_TOKEN);

        // Assert
        assertTrue(foundCity.isPresent());
        assertEquals("Los Angeles", foundCity.get().getName());
        assertEquals(new State("CA"), foundCity.get().getState());
    }

    @Test
    void shouldReturnEmptyWhenCityNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<City> foundCity = adapter.findByUid(nonExistentId, TEST_TOKEN);

        // Assert
        assertFalse(foundCity.isPresent());
    }

    @Test
    void shouldFindAllCities() {
        // Arrange
        adapter.save(new City(CityId.newId(), "Boston", new State("MA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Chicago", new State("IL")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Denver", new State("CO")), TEST_TOKEN);

        // Act
        List<City> cities = adapter.findAll(TEST_TOKEN);

        // Assert
        assertNotNull(cities);
        assertEquals(3, cities.size());
    }

    @Test
    void shouldUpdateCity() {
        // Arrange
        City city = new City(CityId.newId(), "Seattle", new State("WA"));
        City savedCity = adapter.save(city, TEST_TOKEN);
        UUID cityId = savedCity.getId().value();

        // Create updated city
        City updatedCity = new City(savedCity.getId(), "Seattle Updated", new State("WA"));

        // Act
        City result = adapter.update(cityId, updatedCity, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals("Seattle Updated", result.getName());
        
        // Verify in database
        Optional<City> retrievedCity = adapter.findByUid(cityId, TEST_TOKEN);
        assertTrue(retrievedCity.isPresent());
        assertEquals("Seattle Updated", retrievedCity.get().getName());
    }

    @Test
    void shouldDeleteCity() {
        // Arrange
        City city = new City(CityId.newId(), "Portland", new State("OR"));
        City savedCity = adapter.save(city, TEST_TOKEN);
        UUID cityId = savedCity.getId().value();

        // Act
        adapter.deleteByUid(cityId, TEST_TOKEN);

        // Assert
        Optional<City> foundCity = adapter.findByUid(cityId, TEST_TOKEN);
        assertFalse(foundCity.isPresent());
    }

    @Test
    void shouldFindAllWithPagination() {
        // Arrange - Create 15 cities
        for (int i = 1; i <= 15; i++) {
            String cityName = "City" + getLetter(i);  // CityA, CityB, etc.
            String stateName = "ST" + getLetter(i);
            adapter.save(new City(CityId.newId(), cityName, new State(stateName)), TEST_TOKEN);
        }

        // Act - Get page 1 with size 5
        PageResult<City> page1 = adapter.findAllWithPagination(1, 5, null, "name,asc", TEST_TOKEN);

        // Assert
        assertNotNull(page1);
        assertEquals(5, page1.content().size());
        assertEquals(1, page1.page());
        assertEquals(5, page1.size());
        assertEquals(15L, page1.totalElements());
        assertEquals(3, page1.totalPages());

        // Act - Get page 2
        PageResult<City> page2 = adapter.findAllWithPagination(2, 5, null, "name,asc", TEST_TOKEN);

        // Assert
        assertNotNull(page2);
        assertEquals(5, page2.content().size());
        assertEquals(2, page2.page());
    }
    
    // Helper method to get letters for city names
    private String getLetter(int index) {
        if (index <= 26) {
            return String.valueOf((char) ('A' + index - 1));
        } else {
            int first = (index - 1) / 26;
            int second = (index - 1) % 26;
            return String.valueOf((char) ('A' + first - 1)) + (char) ('A' + second);
        }
    }

    @Test
    void shouldFindAllWithSearch() {
        // Arrange
        adapter.save(new City(CityId.newId(), "San Francisco", new State("CA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "San Diego", new State("CA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Austin", new State("TX")), TEST_TOKEN);

        CityFilterQuery query = new CityFilterQuery(null, 1, 10, "San", null);

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertTrue(result.content().stream().allMatch(c -> c.getName().contains("San")));
    }

    @Test
    void shouldFindAllWithFilters() {
        // Arrange
        adapter.save(new City(CityId.newId(), "Miami", new State("FL")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Tampa", new State("FL")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Phoenix", new State("AZ")), TEST_TOKEN);

        // Create filter for FL state
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "state",
                CityFilterQuery.Operator.EQUALS,
                "FL"
        );

        CityFilterQuery.FilterGroup filterGroup = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND,
                List.of(condition)
        );

        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND,
                List.of(filterGroup)
        );

        CityFilterQuery query = new CityFilterQuery(filter, 1, 10, null, null);

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertTrue(result.content().stream().allMatch(c -> c.getState().equals(new State("FL"))));
    }

    @Test
    void shouldSortCitiesAscending() {
        // Arrange
        adapter.save(new City(CityId.newId(), "Zebra City", new State("ZZ")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Alpha City", new State("AA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Beta City", new State("BB")), TEST_TOKEN);

        CityFilterQuery.SortOrder sortOrder = new CityFilterQuery.SortOrder(
                "name",
                CityFilterQuery.Direction.ASC
        );

        CityFilterQuery query = new CityFilterQuery(null, 1, 10, null, List.of(sortOrder));

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.content().size());
        assertEquals("Alpha City", result.content().get(0).getName());
        assertEquals("Beta City", result.content().get(1).getName());
        assertEquals("Zebra City", result.content().get(2).getName());
    }

    @Test
    void shouldSortCitiesDescending() {
        // Arrange
        adapter.save(new City(CityId.newId(), "Zebra City", new State("ZZ")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Alpha City", new State("AA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Beta City", new State("BB")), TEST_TOKEN);

        CityFilterQuery.SortOrder sortOrder = new CityFilterQuery.SortOrder(
                "name",
                CityFilterQuery.Direction.DESC
        );

        CityFilterQuery query = new CityFilterQuery(null, 1, 10, null, List.of(sortOrder));

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.content().size());
        assertEquals("Zebra City", result.content().get(0).getName());
        assertEquals("Beta City", result.content().get(1).getName());
        assertEquals("Alpha City", result.content().get(2).getName());
    }

    @Test
    void shouldHandleLikeFilterOperator() {
        // Arrange
        adapter.save(new City(CityId.newId(), "New York", new State("NY")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "New Orleans", new State("LA")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Old Town", new State("ME")), TEST_TOKEN);

        // Create filter with LIKE operator
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name",
                CityFilterQuery.Operator.LIKE,
                "New"
        );

        CityFilterQuery.FilterGroup filterGroup = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND,
                List.of(condition)
        );

        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND,
                List.of(filterGroup)
        );

        CityFilterQuery query = new CityFilterQuery(filter, 1, 10, null, null);

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertTrue(result.content().stream().allMatch(c -> c.getName().contains("New")));
    }

    @Test
    void shouldHandleComplexFiltersWithMultipleConditions() {
        // Arrange
        adapter.save(new City(CityId.newId(), "Dallas", new State("TX")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Houston", new State("TX")), TEST_TOKEN);
        adapter.save(new City(CityId.newId(), "Denver", new State("CO")), TEST_TOKEN);

        // Create complex filter: (state = TX AND name LIKE "Dal")
        CityFilterQuery.FilterCondition condition1 = new CityFilterQuery.FilterCondition(
                "state",
                CityFilterQuery.Operator.EQUALS,
                "TX"
        );

        CityFilterQuery.FilterCondition condition2 = new CityFilterQuery.FilterCondition(
                "name",
                CityFilterQuery.Operator.LIKE,
                "Dal"
        );

        CityFilterQuery.FilterGroup filterGroup = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND,
                List.of(condition1, condition2)
        );

        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND,
                List.of(filterGroup)
        );

        CityFilterQuery query = new CityFilterQuery(filter, 1, 10, null, null);

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, TEST_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("Dallas", result.content().get(0).getName());
        assertEquals(new State("TX"), result.content().get(0).getState());
    }

    @Test
    void shouldPersistAuditingInformation() {
        // Arrange
        City city = new City(CityId.newId(), "Philadelphia", new State("PA"));

        // Act
        City savedCity = adapter.save(city, TEST_TOKEN);

        // Assert - Verify the city was saved
        assertNotNull(savedCity);
        
        // Verify audit fields are set at database level
        Optional<CityEntity> entity = repository.findByUid(savedCity.getId().value().toString());
        assertTrue(entity.isPresent());
        assertNotNull(entity.get().getCreatedAt());
        assertNotNull(entity.get().getUpdatedAt());
    }
}
