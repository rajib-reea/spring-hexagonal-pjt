# Hexagonal Project

This repository contains a small hexagonal-architecture Java service.

## 📊 Documentation

### 📑 [Comprehensive Presentation →](PRESENTATION.md)

**NEW!** Complete presentation covering the entire project:
- Introduction and architectural concepts
- Technology stack and design patterns
- Layer-by-layer breakdown with code examples
- API documentation and usage
- Setup instructions and best practices
- Testing strategy and future enhancements

### 🏗️ [Architecture Block Diagram →](Architecture.md)

Technical architecture diagrams including:
- Overall hexagonal architecture structure
- Layer-by-layer component breakdown
- Data flow sequence diagrams
- Complete file organization
- Key design patterns and principles

### 🏗️ [Hexagonal Architecture Analysis →](HEXAGONAL_ARCHITECTURE_ANALYSIS.md)

Technical architecture diagrams including:
- Overall hexagonal architecture structure
- Layer-by-layer component breakdown
- Data flow sequence diagrams
- Complete file organization
- Key design patterns and principles

### 🧪 [Testing Strategy & Coverage →](TESTING.md)

Comprehensive testing documentation covering:
- Testing strategy and approach (unit, integration, E2E)
- Test coverage summary (149 total tests)
- Layer-by-layer test breakdown
- Test infrastructure and configuration
- Running tests and best practices

## Run the Project

**Prerequisites:**
- Java 25 or higher (LTS version recommended)
- Maven 3.6 or higher

This project uses JDK 21 and requires it to be available on your system.

**Quick Start:**
```bash
mvn clean spring-boot:run
```

**Build and run (requires Maven and Java):**
```bash
mvn -DskipTests package
java -jar target/*.jar
```
## Notes

- The project uses Spring Boot with WebFlux and Spring Data JPA.
- Logging is configured via `logback-spring.xml` and writes to `logs/app.log`.
- API examples are defined in `CitySpec` and exposed with springdoc (Swagger UI).

Feel free to edit this README; let me know if you want the tree in a different format.

## [Not Implemented] External API Integration (third-party APIs)

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


