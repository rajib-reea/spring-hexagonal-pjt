package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.model.City;
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
                log.debug("createdAt was null — set to now: {}", entity.getCreatedAt());
            }

            log.info("Persisting CityEntity: uid={}, name={}, state={}, isActive={}",
                    entity.getUid(), entity.getName(), entity.getState(), entity.getIsActive());

            CityEntity saved = repo.save(entity);

            log.info("Saved CityEntity with id={}, uid={}, createdAt={}", saved.getId(), saved.getUid(), saved.getCreatedAt());

            return CityMapper.toModel(saved);

        } catch (DataAccessException ex) {
            log.error("Database error while saving City [uid={}]", city.getId(), ex);
            throw new DatabaseException("Failed to save City", ex);
        }
    }

    @Override
    public List<City> findAll(String token) {
        try {
            return repo.findAll()
                    .stream()
                    .map(CityMapper::toModel)
                    .toList();
        } catch (DataAccessException ex) {
            log.error("Database error while fetching all cities", ex);
            throw new DatabaseException("Failed to fetch cities", ex);
        }
    }

    @Override
    public Optional<City> findByUid(UUID uid, String token) {
        try {
            log.info("Received UUID for uid={}", uid);
            return repo.findByUid(String.valueOf(uid))
                    .map(CityMapper::toModel);

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
            // Determine sorting
            Sort sortObj;
            String[] sortParts = sort.split(",");
            if (sortParts.length == 2 && sortParts[1].equalsIgnoreCase("desc")) {
                sortObj = Sort.by(sortParts[0]).descending();
            } else {
                sortObj = Sort.by(sortParts[0]).ascending();
            }

            Pageable pageable = PageRequest.of(page, size, sortObj);

            Page<CityEntity> result;

            if (search == null || search.isBlank()) {
                result = repo.findAll(pageable);
            } else {
                result = repo.findByNameOrState(search, search, pageable);
            }
            log.info("Result of pagination: page {} of {} with total elements {}",
                    result.getNumber(), result.getTotalPages(), result.getTotalElements());

            // If there are no results, always return empty list
            if (result.getTotalPages() == 0) {
                return List.of(); // no exception
            }
            if (page >= result.getTotalPages()) {
                throw new IllegalArgumentException(
                        String.format("Requested page %d exceeds total pages %d", page+1, result.getTotalPages())
                );
            }
            return result.stream().map(CityMapper::toModel).toList();

        } catch (DataAccessException ex) {
            log.error("Database error while fetching cities with pagination", ex);
            throw new DatabaseException("Failed to fetch paginated cities", ex);
        }
    }
}
