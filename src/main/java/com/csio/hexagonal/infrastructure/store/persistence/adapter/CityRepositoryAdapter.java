package com.csio.hexagonal.infrastructure.store.persistence.adapter;


import com.csio.hexagonal.infrastructure.store.persistence.mapper.CityMapper;
import com.csio.hexagonal.application.port.out.CityPersistencePort;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.store.persistence.repo.CityRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CityRepositoryAdapter implements CityPersistencePort {

    private final CityRepository repo;
    private static final Logger log = LoggerFactory.getLogger(CityRepositoryAdapter.class);

    public CityRepositoryAdapter(CityRepository repo) {
        this.repo = repo;
    }

    @Override
    public City save(City city, String token) {
        CityEntity entity = CityMapper.toEntity(city);
        // Fallback: ensure createdAt is populated if JPA auditing didn't run
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(java.time.LocalDateTime.now());
            log.debug("createdAt was null — set to now: {}", entity.getCreatedAt());
        }

        log.info("Persisting CityEntity: uid={}, name={}, state={}, isActive={}",
                entity.getUid(), entity.getName(), entity.getState(), entity.getIsActive());

        CityEntity saved = repo.save(entity);

        log.info("Saved CityEntity with id={}, uid={}, createdAt={}", saved.getId(), saved.getUid(), saved.getCreatedAt());

        City model = CityMapper.toModel(saved);
        log.info("Mapped saved entity to model: {}", model);
        return model;
    }

    @Override
    public List<City> findAll(String token) {
        // token can be ignored if not used in DB layer
        return repo.findAll()
                .stream()
                .map(CityMapper::toModel)
                .toList();
    }

    @Override
    public Optional<City> findByUid(UUID uid, String token) {
        log.info("Received UUID for uid={}", uid);
        return repo.findByUid(String.valueOf(uid)).map(CityMapper::toModel);
    }

    @Override
    public City update(String uid, City entity, String token) {
        // naive implementation: map to entity and save
        CityEntity e = CityMapper.toEntity(entity);
        CityEntity saved = repo.save(e);
        return CityMapper.toModel(saved);
    }

    @Override
    public void deleteByUid(String uid, String token) {
        repo.deleteById(uid);
    }
}