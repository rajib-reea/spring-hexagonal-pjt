package com.csio.hexagonal.infrastructure.store.persistence.out.adapter;


import com.csio.hexagonal.infrastructure.store.persistence.mapper.CityMapper;
import com.csio.hexagonal.application.port.out.CityOutPort;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import com.csio.hexagonal.infrastructure.store.persistence.repo.CityRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Repository
public class CityRepositoryAdapter implements CityOutPort {

    private final CityRepository repo;
    private static final Logger log = LoggerFactory.getLogger(CityRepositoryAdapter.class);

    public CityRepositoryAdapter(CityRepository repo) {
        this.repo = repo;
    }

    @Override
    public City save(City city) {
        CityEntity entity = CityMapper.toEntity(city);
        // Fallback: ensure createdAt is populated if JPA auditing didn't run
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
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
    public List<City> findAll() {
        return repo.findAll()
                   .stream()
                   .map(CityMapper::toModel)
                   .toList();
    }
}
