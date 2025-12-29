## Run the Project:

This repo uses jdk version 25.
````
mvn clean spring-boot:run
````
# Hexagonal Project

This repository contains a small hexagonal-architecture Java service.

## Run the Project

Build and run (requires Maven and Java):

```bash
mvn -DskipTests package
java -jar target/*.jar
```

## Project Tree

```text
- pom.xml
- ReadMe.md
- src/
    +- main/
        +- java/
            +- com/csio/hexagonal/
                +- CityServiceApplication.java
                +- application/
                    +- port/
                        +- in/
                            +- CommandUseCase.java
                            +- QueryUseCase.java
                    +- service/
                        +- CityService.java
                    +- usecase/
                        +- CreateCityCommand.java
                +- domain/
                    +- exception/
                        +- DuplicateCityException.java
                        +- InvalidCityNameException.java
                    +- model/
                        +- City.java
                    +- policy/
                        +- city/
                            +- CityPolicy.java
                            +- CityPolicyEnforcer.java
                    +- specification/
                        +- CityNameNotEmptySpec.java
                    +- vo/
                        +- CityId.java
                        +- State.java
                +- infrastructure/
                    +- config/
                        +- executor/
                            +- AsyncExecutorProperties.java
                            +- PlatformTaskExecutorConfig.java
                            +- VirtualThreadExecutorConfig.java
                        +- router/
                            +- group/
                                +- CityGroup.java
                            +- operation/
                                +- city/
                                    +- CityRouter.java
                        +- AuditingConfig.java
                    +- rest/
                        +- handler/
                            +- CityHandler.java
                        +- mapper/
                            +- CityMapper.java
                        +- request/
                            +- CreateCityRequest.java
                        +- response/
                            +- city/ (response DTOs)
                        +- spec/
                            +- CitySpec.java
                        +- validator/
                            +- Validator.java
                    +- store/
                        +- persistence/
                            +- entity/
                                +- AuditableEntity.java
                                +- CityEntity.java
                            +- mapper/
                                +- CityMapper.java
                            +- repo/
                                +- CityRepository.java
                            +- out/
                                +- adapter/
                                    +- CityRepositoryAdapter.java
        +- resources/
            +- application.properties
            +- logback-spring.xml
- target/ (build output)
```

## Notes

- The project uses Spring Boot with WebFlux and Spring Data JPA.
- Logging is configured via `logback-spring.xml` and writes to `logs/app.log`.
- API examples are defined in `CitySpec` and exposed with springdoc (Swagger UI).

Feel free to edit this README; let me know if you want the tree in a different format.
