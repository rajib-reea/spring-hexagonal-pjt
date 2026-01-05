package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, String> {
    //this method exists as there is no method for uid defined in jpa repository
    Optional<CityEntity> findByUid(String uid);
    Page<CityEntity> findByNameOrState(String name, String state, Pageable pageable);
}
