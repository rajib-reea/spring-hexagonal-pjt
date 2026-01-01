package com.csio.hexagonal.infrastructure.store.persistence.repo;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, String> {}
