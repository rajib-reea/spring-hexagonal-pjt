package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.store.persistence.exception.DatabaseException;
import com.csio.hexagonal.infrastructure.store.persistence.mapper.CityMapper;
import com.csio.hexagonal.infrastructure.store.persistence.specification.CitySpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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

            return result.stream().map(CityMapper::toModel).toList();
        } catch (DataAccessException ex) {
            log.error("Database error while fetching cities with pagination", ex);
            throw new DatabaseException("Failed to fetch paginated cities", ex);
        }
    }

    @Override
    public List<City> findAllWithFilters(CityFindAllRequest request, String token) {
        try {
            // Build sort object for pageable
            Sort sortObj = buildSortObject(request.sort());
            PageRequest pageable = PageRequest.of(request.page(), request.size(), sortObj);

            // Build Specification using the top-level Filter object
            Specification<CityEntity> spec = CitySpecification.buildSpecification(
                    request.search(), request.filter()
            );

            log.info("Fetching cities with Specification and pagination: page={}, size={}", request.page(), request.size());

            Page<CityEntity> pageResult = repo.findAll(spec, pageable);

            return pageResult.stream()
                    .map(CityMapper::toModel)
                    .toList();

        } catch (DataAccessException ex) {
            log.error("Database error while fetching cities with filters", ex);
            throw new DatabaseException("Failed to fetch filtered cities", ex);
        }
    }

    private Sort buildSortObject(List<CityFindAllRequest.SortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return Sort.by("name").ascending();
        }

        Sort sort = Sort.by(sortOrders.getFirst().field());
        sort = sortOrders.getFirst().direction() == CityFindAllRequest.Direction.DESC
                ? sort.descending()
                : sort.ascending();

        for (int i = 1; i < sortOrders.size(); i++) {
            CityFindAllRequest.SortOrder so = sortOrders.get(i);
            sort = sort.and(so.direction() == CityFindAllRequest.Direction.DESC
                    ? Sort.by(so.field()).descending()
                    : Sort.by(so.field()).ascending());
        }

        return sort;
    }

}
