
package dev.educosta.infrastructure.store.persistence.out.adaptor;

import dev.educosta.application.port.out.CityRepository;
import dev.educosta.domain.model.City;
import dev.educosta.domain.vo.CityId;
import dev.educosta.domain.vo.State;
import dev.educosta.infrastructure.store.persistence.entity.CityJpaEntity;
import dev.educosta.infrastructure.store.persistence.repo.SpringCityJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CityRepositoryAdapter implements CityRepository {

    private final SpringCityJpaRepository repo;

    public CityRepositoryAdapter(SpringCityJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void save(City city) {
        CityJpaEntity e = new CityJpaEntity();
        e.id = city.id().value();
        e.name = city.name();
        e.state = city.state().value();
        repo.save(e);
    }

    @Override
    public List<City> findAll() {
        return repo.findAll().stream()
                .map(e -> new City(
                        new CityId(e.id),
                        e.name,
                        new State(e.state)
                ))
                .toList();
    }
}
