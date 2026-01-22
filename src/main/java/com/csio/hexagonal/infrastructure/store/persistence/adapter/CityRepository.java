package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Long>, 
        JpaSpecificationExecutor<CityEntity> {

    // Find by UID since it's not the primary key
    Optional<CityEntity> findByUid(String uid);

}