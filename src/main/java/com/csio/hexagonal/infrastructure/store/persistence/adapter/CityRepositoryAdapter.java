package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.store.persistence.exception.DatabaseException;
import com.csio.hexagonal.infrastructure.store.persistence.mapper.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CityRepositoryAdapter implements CityServiceContract {

    private static final Logger log = LoggerFactory.getLogger(CityRepositoryAdapter.class);

    private final CityRepository repo;

    public CityRepositoryAdapter(CityRepository repo) {
        this.repo = repo;
    }

    @Override
    public City save(City city, String token) {
        try {
            CityEntity entity = CityMapper.toEntity(city);
            if (entity.getCreatedAt() == null) {
                entity.setCreatedAt(LocalDateTime.now());
            }
            log.info("Persisting CityEntity: uid={}, name={}, state={}, isActive={}",
                    entity.getUid(), entity.getName(), entity.getState(), entity.getIsActive());
            CityEntity saved = repo.save(entity);
            return CityMapper.toModel(saved);
        } catch (DataAccessException ex) {
            log.error("Database error while saving City [uid={}]", city.getId(), ex);
            throw new DatabaseException("Failed to save City", ex);
        }
    }

    @Override
    public List<City> findAll(String token) {
        try {
            return repo.findAll().stream().map(CityMapper::toModel).toList();
        } catch (DataAccessException ex) {
            log.error("Database error while fetching all cities", ex);
            throw new DatabaseException("Failed to fetch cities", ex);
        }
    }

    @Override
    public Optional<City> findByUid(UUID uid, String token) {
        try {
            log.info("Received UUID for uid={}", uid);
            return repo.findByUid(String.valueOf(uid)).map(CityMapper::toModel);
        } catch (DataAccessException ex) {
            log.error("Database error while fetching City [uid={}]", uid, ex);
            throw new DatabaseException("Failed to fetch City", ex);
        }
    }

    @Override
    public City update(UUID uid, City city, String token) {
        try {
            CityEntity entity = CityMapper.toEntity(city);
            CityEntity saved = repo.save(entity);
            return CityMapper.toModel(saved);
        } catch (DataAccessException ex) {
            log.error("Database error while updating City [uid={}]", uid, ex);
            throw new DatabaseException("Failed to update City", ex);
        }
    }

    @Override
    public void deleteByUid(UUID uid, String token) {
        try {
            repo.deleteById(uid.toString());
        } catch (DataAccessException ex) {
            log.error("Database error while deleting City [uid={}]", uid, ex);
            throw new DatabaseException("Failed to delete City", ex);
        }
    }

    @Override
    public List<City> findAllWithPagination(int page, int size, String search, String sort, String token) {
        try {
            Sort sortObj;
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2 && sortParts[1].equalsIgnoreCase("desc")) {
                sortObj = Sort.by(sortParts[0]).descending();
            } else {
                sortObj = Sort.by(sortParts[0]).ascending();
            }

            Pageable pageable = PageRequest.of(page, size, sortObj);
            Page<CityEntity> result = (search == null || search.isBlank())
                    ? repo.findAll(pageable)
                    : repo.findByNameOrState(search, pageable);

            if (result.getTotalPages() == 0) return List.of();
            if (page >= result.getTotalPages())
                throw new IllegalArgumentException(
                        String.format("Requested page %d exceeds total pages %d", page + 1, result.getTotalPages())
                );

            return result.stream().map(CityMapper::toModel).toList();
        } catch (DataAccessException ex) {
            log.error("Database error while fetching cities with pagination", ex);
            throw new DatabaseException("Failed to fetch paginated cities", ex);
        }
    }

    @Override
    public List<City> findAllWithFilters(CityFindAllRequest request, String token) {
        try {
            // Use pagination if no filters
            if (request.filterGroups() == null || request.filterGroups().isEmpty()) {
                log.info("No filters provided, delegating to findAllWithPagination");
                return findAllWithPagination(
                        request.page(),
                        request.size(),
                        request.search(),
                        buildSortString(request.sort()),
                        token
                );
            }

            // Fetch all entities
            List<CityEntity> entities = repo.findAll();

            // Apply filters in memory
            var filtered = entities.stream().filter(entity -> {
                boolean matches = true;
                for (var group : request.filterGroups()) {
                    boolean groupResult = group.operator() == CityFindAllRequest.LogicalOperator.AND;
                    for (var condition : group.conditions()) {
                        boolean conditionResult = isConditionResult(entity, condition);
                        if (group.operator() == CityFindAllRequest.LogicalOperator.AND) {
                            groupResult = groupResult && conditionResult;
                        } else { // OR
                            groupResult = groupResult || conditionResult;
                        }
                    }
                    matches = matches && groupResult; // combine groups with AND
                }
                return matches;
            }).toList();

            // TODO: implement sorting and pagination if needed
            return filtered.stream().map(CityMapper::toModel).toList();
        } catch (DataAccessException ex) {
            log.error("Database error while fetching cities with filters", ex);
            throw new DatabaseException("Failed to fetch filtered cities", ex);
        }
    }

    private static boolean isConditionResult(CityEntity entity, CityFindAllRequest.FilterCondition condition) {
        boolean conditionResult = true;
        switch (condition.field()) {
            case "state" -> conditionResult = entity.getState().equalsIgnoreCase(condition.value());
            case "active" -> conditionResult = entity.getIsActive().toString().equalsIgnoreCase(condition.value());
            case "name" -> {
                if (condition.operator() == CityFindAllRequest.Operator.LIKE) {
                    conditionResult = entity.getName().toLowerCase().contains(condition.value().toLowerCase());
                } else if (condition.operator() == CityFindAllRequest.Operator.EQUALS) {
                    conditionResult = entity.getName().equalsIgnoreCase(condition.value());
                }
            }
        }
        return conditionResult;
    }

    // Helper to build "field,direction" string for pagination
    private String buildSortString(List<CityFindAllRequest.SortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) return "name,asc";
        var sb = new StringBuilder();
        var first = sortOrders.getFirst();
        sb.append(first.field()).append(",").append(first.direction().name());
        return sb.toString();
    }
}
