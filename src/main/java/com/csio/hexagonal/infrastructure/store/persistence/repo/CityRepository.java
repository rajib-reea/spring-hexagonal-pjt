package com.csio.hexagonal.infrastructure.store.persistence.repo;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<CityEntity, String> {
    Optional<CityEntity> findByUid(String uid);
}
