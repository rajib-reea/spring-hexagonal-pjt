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
@Repository
public class CityRepositoryAdapter implements CityOutPort {

    private final CityRepository repo;

    public CityRepositoryAdapter(CityRepository repo) {
        this.repo = repo;
    }

    @Override
    public City save(City city) {
        CityEntity entity = CityMapper.toEntity(city);
        CityEntity saved = repo.save(entity);
        return CityMapper.toModel(saved);
    }

    @Override
    public List<City> findAll() {
        return repo.findAll()
                   .stream()
                   .map(CityMapper::toModel)
                   .toList();
    }
}
