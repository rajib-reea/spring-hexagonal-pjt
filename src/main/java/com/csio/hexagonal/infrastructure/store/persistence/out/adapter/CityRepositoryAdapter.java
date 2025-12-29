
// package com.csio.hexagonal.infrastructure.store.persistence.out.adapter;

// import com.csio.hexagonal.application.port.out.CityOutPort;
// import com.csio.hexagonal.domain.model.City;
// import com.csio.hexagonal.domain.vo.CityId;
// import com.csio.hexagonal.domain.vo.State;
// import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
// import com.csio.hexagonal.infrastructure.store.persistence.repo.CityRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public class CityRepositoryAdapter implements CityOutPort {

//     private final CityRepository repo;

//     public CityRepositoryAdapter(CityRepository repo) {
//         this.repo = repo;
//     }

//     @Override
//     public City save(City city) {
//         CityEntity e = new CityEntity();
//         e.id = city.id().value();
//         e.name = city.name();
//         e.state = city.state().value();
//         repo.save(e);
//         return city;
//     }

//     @Override
//     public List<City> findAll() {
//         return repo.findAll().stream()
//                 .map(e -> new City(
//                         new CityId(e.id),
//                         e.name,
//                         new State(e.state)
//                 ))
//                 .toList();
//     }
// }
// @Repository
// public class CityRepositoryAdapter implements CityOutPort {

//     private final CityRepository repo;

//     public CityRepositoryAdapter(CityRepository repo) {
//         this.repo = repo;
//     }

//     @Override
//     public City save(City city) {
//         CityEntity entity = CityMapper.toEntity(city);
//         CityEntity saved = repo.save(entity);
//         return CityMapper.toDomain(saved);
//     }

//     @Override
//     public List<City> findAll() {
//         return repo.findAll()
//                    .stream()
//                    .map(CityMapper::toDomain)
//                    .toList();
//     }
// }

package com.csio.hexagonal.infrastructure.store.persistence.out.adapter;


import com.csio.hexagonal.infrastructure.store.persistence.mapper.CityPersistenceMapper;
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
        CityEntity entity = CityPersistenceMapper.toEntity(city);
        CityEntity saved = repo.save(entity);
        return CityPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<City> findAll() {
        return repo.findAll()
                   .stream()
                   .map(CityPersistenceMapper::toDomain)
                   .toList();
    }
}
