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

## Call Flow (Swagger request → DB save)**

- **Client → Swagger UI / HTTP POST:** send POST /api/v1/city with example body (see [src/main/java/com/csio/hexagonal/infrastructure/rest/spec/CitySpec.java](src/main/java/com/csio/hexagonal/infrastructure/rest/spec/CitySpec.java)).
- **Router:** request routed by [src/main/java/com/csio/hexagonal/infrastructure/config/router/operation/city/CityRouter.java](src/main/java/com/csio/hexagonal/infrastructure/config/router/operation/city/CityRouter.java) to the handler.
- **Handler:** [src/main/java/com/csio/hexagonal/infrastructure/rest/handler/CityHandler.java](src/main/java/com/csio/hexagonal/infrastructure/rest/handler/CityHandler.java) reads `CreateCityRequest`, maps to `CreateCityCommand`, logs, and calls `commandUseCase.create(...)` (reactive `Mono`) — the handler subscribes the flow on the `virtualExecutor` to avoid blocking the event loop.
- **Application / Use Case:** [src/main/java/com/csio/hexagonal/application/service/CityService.java](src/main/java/com/csio/hexagonal/application/service/CityService.java) implements the create use case and returns `Mono<CityResponse>`; it explicitly offloads blocking work: `findAll()` is run on `virtualExecutor`, CPU-bound `cityPolicy.ensureUnique(...)` runs on `cpuExecutor`, and persistence is performed on `virtualExecutor`.
- **Port / Adapter:** the service calls the persistence port interface [src/main/java/com/csio/hexagonal/application/port/out/CityPersistencePort.java](src/main/java/com/csio/hexagonal/application/port/out/CityPersistencePort.java) which is implemented by [src/main/java/com/csio/hexagonal/infrastructure/store/persistence/out/adapter/CityRepositoryAdapter.java](src/main/java/com/csio/hexagonal/infrastructure/store/persistence/out/adapter/CityRepositoryAdapter.java). The adapter maps domain model ↔ entity and calls the blocking `CityRepository`.
- **Repository → Database:** [src/main/java/com/csio/hexagonal/infrastructure/store/persistence/repo/CityRepository.java](src/main/java/com/csio/hexagonal/infrastructure/store/persistence/repo/CityRepository.java) is a Spring Data JPA `JpaRepository` that performs blocking JDBC operations to persist the entity into the DB. JPA auditing populates timestamps (see [src/main/java/com/csio/hexagonal/infrastructure/store/persistence/entity/AuditableEntity.java](src/main/java/com/csio/hexagonal/infrastructure/store/persistence/entity/AuditableEntity.java)).
- **Observability:** important artifacts: logs written to `logs/app.log` (configured in [src/main/resources/logback-spring.xml](src/main/resources/logback-spring.xml)) and OpenAPI examples in `CitySpec` used by Swagger UI.

Keep blocking adapters scheduled on `virtualExecutor` so the Netty event loop remains non-blocking. If you want, I can add a short sequence diagram or Mermaid block next.


## External API Integration (third-party APIs)

Recommended steps to add a third‑party API (e.g. Meta API, Google API):

- **Define a port (outbound interface):** create an interface under `application.port.out` (for example `MetaApiPort`) that expresses the operations your application needs.
- **Implement an adapter/client in infrastructure:** add a reactive client under `infrastructure.integration` or `infrastructure.adapter` (for example `MetaApiClient`), implementing the port. Prefer `WebClient` and return `Mono`/`Flux` for WebFlux projects.
- **Configuration & beans:** put base URLs, timeouts and credentials in `application.properties` and bind them with a `@ConfigurationProperties` class under `infrastructure.config`. Provide a `WebClient` bean if needed.
- **Non‑blocking by default:** use `WebClient` (reactive) so calls stay non‑blocking. If you must use a blocking client, offload calls to the `virtualExecutor` with `Mono.fromCallable(...).subscribeOn(Schedulers.fromExecutor(virtualExecutor))`.
- **CPU vs blocking separation:** keep blocking I/O on `virtualExecutor` and CPU‑heavy work on `cpuExecutor` (the existing `cpuExecutor` bean in `PlatformTaskExecutorConfig`).
- **Resilience & observability:** add timeouts, retries and circuit breakers (Resilience4j) at the adapter level; instrument calls with logs/metrics.
- **Domain mapping:** adapter maps third‑party DTOs to domain models or application commands; services depend only on the `MetaApiPort` abstraction.
- **Testing:** unit‑test services by mocking the port; integration tests can use `WireMock` or a test `WebClient` server.

Example minimal structure:

```text
application/port/out/MetaApiPort.java      # port (interface)
infrastructure/integration/meta/MetaApiClient.java   # WebClient-based adapter implements port
infrastructure/config/MetaApiConfig.java   # WebClient bean + properties binding
```

Add these steps where appropriate in your workflow and inject the port into services rather than the concrete client.