## Run the Project:

This repo uses jdk version 21.
````
mvn clean spring-boot:run
````

## Main Motto:

application orchestrates → domain decides → infrastructure executes

## Structure:
```text
domain
-model
-exception
-service
-specification
-VO
infrastructure
-rest
	-request
	-response
	-mapper
	-in
		-adaptor[uses usecase]
-store
	-persistence
		-entity
		-repo
		-out
			-adaptor[implements out port and uses model]
application
-port
	-in[implements usecase, uses out port]
	-out[this is an interface]
-usecase[this is an interface]

## This Particular Service:

city-service
├── build.gradle
├── settings.gradle
└── src
    └── main
        └── java
            └── com.csio.hexagonal
                ├── CityServiceApplication.java
                │
                ├── domain
                │   ├── model
                │   │   └── City.java
                │   ├── service
                │   │   ├── CityUniquenessChecker.java
                │   │   └── impl
                │   │       └── CityUniquenessCheckerImpl.java
                │   ├── specification
                │   │   └── CityNameNotEmptySpec.java
                │   ├── exception
                │   │   ├── DuplicateCityException.java
                │   │   └── InvalidCityNameException.java
                │   └── vo
                │       ├── CityId.java
                │       └── State.java
                │
                ├── application
                │   ├── port
                │   │   ├── in
                │   │   │   └── CreateCityUseCase.java
                │   │   └── out
                │   │       └── CityRepository.java
                │   ├── usecase
                │   │   └── CreateCityCommand.java
                │   └── service
                │       └── CreateCityService.java
                │
                └── infrastructure
                    ├── rest
                    │   ├── request
                    │   │   └── CreateCityRequest.java
                    │   ├── mapper
                    │   │   └── CityRestMapper.java
                    │   └── in
                    │       └── adaptor
                    │           └── CityController.java
                    │
                    ├── store
                    │   └── persistence
                    │       ├── entity
                    │       │   └── CityJpaEntity.java
                    │       ├── repo
                    │       │   └── SpringCityJpaRepository.java
                    │       └── out
                    │           └── adaptor
                    │               └── CityRepositoryAdapter.java
                    │
                    └── config
                        └── DomainConfig.java
```

## Some Code:

````
City (Aggregate Root)
package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.vo.Id;
import com.csio.hexagonal.domain.vo.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class City {

    private final Id id;
    private String name;
    private final State state;

    private final List<Address> addresses = new ArrayList<>();

    public City(Id id, String name, State state) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }
        this.id = id;
        this.name = name;
        this.state = state;
    }

    // ===== Aggregate behavior =====

    public void addAddress(Address address) {
        if (!address.belongsTo(state)) {
            throw new IllegalStateException(
                "Address state must match city state"
            );
        }
        addresses.add(address);
    }

    public void removeAddress(AddressId addressId) {
        addresses.removeIf(a -> a.id().equals(addressId));
    }

    // ===== Safe accessors =====

    public List<Address> addresses() {
        return Collections.unmodifiableList(addresses);
    }

    public Id id() {
        return id;
    }
}

Address (Entity inside aggregate)

package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.vo.State;

public class Address {

    private final AddressId id;
    private final String street;
    private final String zip;
    private final State state;

    Address(AddressId id, String street, String zip, State state) {
        this.id = id;
        this.street = street;
        this.zip = zip;
        this.state = state;
    }

    boolean belongsTo(State cityState) {
        return this.state.equals(cityState);
    }

    AddressId id() {
        return id;
    }
}

Use case (application layer)
public class AddAddressToCityUseCase {

    private final CityRepository cityRepository;

    public AddAddressToCityUseCase(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public void execute(AddAddressCommand command) {
        City city = cityRepository.findById(command.cityId());

        Address address = new Address(
            AddressId.newId(),
            command.street(),
            command.zip(),
            new State(command.state())
        );

        city.addAddress(address);

        cityRepository.save(city);
    }
}

JPA entities (infrastructure)
@Entity
@Table(name = "city")
class CityJpaEntity {

    @Id
    String id;

    String name;
    String state;

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    List<AddressJpaEntity> addresses = new ArrayList<>();
}
@Entity
@Table(name = "address")
class AddressJpaEntity {

    @Id
    String id;

    String street;
    String zip;
    String state;
}

Mapper keeps boundaries clean
@Mapper(componentModel = "spring")
public interface CityJpaMapper {
    City toDomain(CityJpaEntity entity);
    CityJpaEntity toEntity(City city);
}
````