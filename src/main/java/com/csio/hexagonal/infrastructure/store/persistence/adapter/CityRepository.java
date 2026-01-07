package com.csio.hexagonal.infrastructure.store.persistence.adapter;

import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, String>, 
        JpaSpecificationExecutor<CityEntity> {  // ✅ Add Specification support

    // Find by UID since it's not the primary key
    Optional<CityEntity> findByUid(String uid);

    // Existing search method for convenience
    @Query("""
        SELECT c FROM CityEntity c 
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) 
           OR LOWER(c.state) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<CityEntity> findByNameOrState(@Param("search") String search, Pageable pageable);

}