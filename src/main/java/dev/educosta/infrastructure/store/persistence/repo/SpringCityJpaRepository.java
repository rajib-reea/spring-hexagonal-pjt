
package dev.educosta.infrastructure.store.persistence.repo;

import dev.educosta.infrastructure.store.persistence.entity.CityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringCityJpaRepository extends JpaRepository<CityJpaEntity, String> {}
