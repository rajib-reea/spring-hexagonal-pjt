package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.store.persistence.exception.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityRepositoryAdapterTest {

    @Mock
    private CityRepository repository;

    private CityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CityRepositoryAdapter(repository);
    }

    @Test
    void shouldSaveCitySuccessfully() {
        // Arrange
        City city = new City(CityId.newId(), "New York", new State("NY"));
        String token = "test-token";

        CityEntity savedEntity = new CityEntity();
        savedEntity.setUid(city.getId().value().toString());
        savedEntity.setName(city.getName());
        savedEntity.setState(city.getState().value());
        savedEntity.setIsActive(true);

        when(repository.save(any(CityEntity.class))).thenReturn(savedEntity);

        // Act
        City result = adapter.save(city, token);

        // Assert
        assertNotNull(result);
        assertEquals(city.getName(), result.getName());
        assertEquals(city.getState(), result.getState());
        verify(repository).save(any(CityEntity.class));
    }

    @Test
    void shouldThrowDatabaseExceptionWhenSaveFails() {
        // Arrange
        City city = new City(CityId.newId(), "New York", new State("NY"));
        String token = "test-token";

        when(repository.save(any(CityEntity.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DatabaseException.class, () -> {
            adapter.save(city, token);
        });
    }

    @Test
    void shouldFindAllCities() {
        // Arrange
        String token = "test-token";
        CityEntity entity1 = createCityEntity("New York", "NY");
        CityEntity entity2 = createCityEntity("Los Angeles", "CA");

        when(repository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        // Act
        List<City> result = adapter.findAll(token);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void shouldThrowDatabaseExceptionWhenFindAllFails() {
        // Arrange
        String token = "test-token";
        when(repository.findAll()).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DatabaseException.class, () -> {
            adapter.findAll(token);
        });
    }

    @Test
    void shouldFindCityByUid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String token = "test-token";
        CityEntity entity = createCityEntity("New York", "NY");
        entity.setUid(uuid.toString());

        when(repository.findByUid(uuid.toString())).thenReturn(Optional.of(entity));

        // Act
        Optional<City> result = adapter.findByUid(uuid, token);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New York", result.get().getName());
        verify(repository).findByUid(uuid.toString());
    }

    @Test
    void shouldReturnEmptyWhenCityNotFoundByUid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String token = "test-token";

        when(repository.findByUid(uuid.toString())).thenReturn(Optional.empty());

        // Act
        Optional<City> result = adapter.findByUid(uuid, token);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldThrowDatabaseExceptionWhenFindByUidFails() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String token = "test-token";

        when(repository.findByUid(uuid.toString()))
                .thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DatabaseException.class, () -> {
            adapter.findByUid(uuid, token);
        });
    }

    @Test
    void shouldUpdateCity() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        City city = new City(new CityId(uuid), "New York", new State("NY"));
        String token = "test-token";

        CityEntity existingEntity = new CityEntity();
        existingEntity.setId(1L);
        existingEntity.setUid(uuid.toString());
        existingEntity.setName("Old Name");
        existingEntity.setState("NY");
        
        CityEntity savedEntity = createCityEntity("New York", "NY");
        savedEntity.setId(1L);
        
        when(repository.findByUid(uuid.toString())).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(CityEntity.class))).thenReturn(savedEntity);

        // Act
        City result = adapter.update(uuid, city, token);

        // Assert
        assertNotNull(result);
        verify(repository).findByUid(uuid.toString());
        verify(repository).save(any(CityEntity.class));
    }

    @Test
    void shouldDeleteCityByUid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String token = "test-token";
        
        CityEntity entity = new CityEntity();
        entity.setId(1L);
        entity.setUid(uuid.toString());

        when(repository.findByUid(uuid.toString())).thenReturn(Optional.of(entity));
        doNothing().when(repository).deleteById(1L);

        // Act
        adapter.deleteByUid(uuid, token);

        // Assert
        verify(repository).findByUid(uuid.toString());
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldFindAllWithPagination() {
        // Arrange
        String token = "test-token";
        CityEntity entity1 = createCityEntity("New York", "NY");
        CityEntity entity2 = createCityEntity("Los Angeles", "CA");
        
        Page<CityEntity> page = new PageImpl<>(Arrays.asList(entity1, entity2), 
                PageRequest.of(0, 10), 2);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        PageResult<City> result = adapter.findAllWithPagination(1, 10, null, "name,asc", token);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(2L, result.totalElements());
    }

    @Test
    void shouldFindAllWithFilters() {
        // Arrange
        String token = "test-token";
        CityFilterQuery query = new CityFilterQuery(null, 1, 10, "New", null);
        
        CityEntity entity = createCityEntity("New York", "NY");
        Page<CityEntity> page = new PageImpl<>(Arrays.asList(entity), 
                PageRequest.of(0, 10), 1);

        when(repository.findAll(org.mockito.ArgumentMatchers.<Specification<CityEntity>>any(), any(Pageable.class))).thenReturn(page);

        // Act
        PageResult<City> result = adapter.findAllWithFilters(query, token);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.page());
    }

    private CityEntity createCityEntity(String name, String state) {
        CityEntity entity = new CityEntity();
        entity.setUid(UUID.randomUUID().toString());
        entity.setName(name);
        entity.setState(state);
        entity.setIsActive(true);
        return entity;
    }
}
