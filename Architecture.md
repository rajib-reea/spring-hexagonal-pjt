# Architecture Block Diagram

This document provides a comprehensive block diagram of the Spring Hexagonal Architecture implementation.

## Overall Architecture Diagram

```mermaid
graph TB
    subgraph "External Layer"
        Client[Client/Swagger UI]
    end

    subgraph "Infrastructure Layer - REST"
        Router[CityRouter<br/>Route Configuration]
        Handler[CityHandler<br/>HTTP Handler]
        RequestDTO[Request Objects<br/>CityCreateRequest<br/>CityFindAllRequest]
        ResponseHelper[ResponseHelper<br/>Response Wrapper Utility]
        Validator[Validator]
        ExceptionHandler[Global Exception Handler]
        APISpec[CitySpec<br/>OpenAPI Specification]
    end

    subgraph "Application Layer"
        subgraph "Ports In"
            CommandUseCase[CommandUseCase Interface]
            QueryUseCase[QueryUseCase Interface]
        end
        
        subgraph "Services"
            CreateCityCommandHandler[CreateCityCommandHandler<br/>Command Handler]
            GetCityQueryHandler[GetCityQueryHandler<br/>Query Handler]
            GetAllCityQueryHandler[GetAllCityQueryHandler<br/>Query Handler]
        end
        
        subgraph "Commands & Queries"
            CreateCityCommand[CreateCityCommand]
            GetCityQuery[GetCityQuery]
            GetAllCityQuery[GetAllCityQuery]
        end
        
        subgraph "Ports Out"
            ServiceContract[ServiceContract Interface]
            CityServiceContract[CityServiceContract Interface]
        end
    end

    subgraph "Domain Layer"
        subgraph "Entities"
            City[City Entity]
        end
        
        subgraph "Value Objects"
            CityId[CityId]
            State[State]
        end
        
        subgraph "Policies"
            CityPolicy[CityPolicy Interface]
            CityPolicyEnforcer[CityPolicyEnforcer<br/>Business Rules]
        end
        
        subgraph "Specifications"
            CityNameSpec[CityNameNotEmptySpec]
        end
        
        subgraph "Exceptions"
            DomainExceptions[Domain Exceptions<br/>InvalidCityNameException<br/>InvalidStateNameException<br/>DuplicateCityException]
        end
    end

    subgraph "Infrastructure Layer - Persistence"
        RepoAdapter[CityRepositoryAdapter<br/>Persistence Adapter]
        EntityMapper[Entity Mapper]
        Repository[CityRepository<br/>JPA Repository]
        Entity[CityEntity<br/>JPA Entity]
        PersistenceExceptions[DatabaseException]
    end

    subgraph "Infrastructure Layer - Configuration"
        ExecutorConfig[Executor Configuration<br/>VirtualThreadExecutor<br/>CPUExecutor]
        AuditingConfig[Auditing Configuration]
        JacksonConfig[Jackson Configuration]
        APIDocConfig[API Documentation<br/>CityGroup<br/>GroupedOpenApiProvider]
    end

    subgraph "Data Store"
        Database[(H2 Database)]
    end

    %% Client to Infrastructure REST
    Client -->|HTTP Request| Router
    Router -->|Route to Handler| Handler
    Handler -->|Maps from| RequestDTO
    Handler -->|Validate| Validator
    APISpec -.->|Defines specs for| Handler
    
    %% Infrastructure REST to Application
    Handler -->|Command| CreateCityCommandHandler
    Handler -->|Query| GetCityQueryHandler
    Handler -->|Query| GetAllCityQueryHandler
    Handler -->|Wrap Response| ResponseHelper
    Handler -.->|Error Handling| ExceptionHandler
    
    %% Application Layer Connections
    CreateCityCommandHandler -.->|implements| CommandUseCase
    GetCityQueryHandler -.->|implements| QueryUseCase
    GetAllCityQueryHandler -.->|implements| QueryUseCase
    CreateCityCommandHandler -->|uses| CreateCityCommand
    GetCityQueryHandler -->|uses| GetCityQuery
    GetAllCityQueryHandler -->|uses| GetAllCityQuery
    CreateCityCommandHandler -->|calls| CityServiceContract
    GetCityQueryHandler -->|calls| CityServiceContract
    GetAllCityQueryHandler -->|calls| CityServiceContract
    CityServiceContract -.->|extends| ServiceContract
    
    %% Application to Domain
    CreateCityCommandHandler -->|creates & validates| City
    CreateCityCommandHandler -->|enforces| CityPolicy
    CityPolicyEnforcer -.->|implements| CityPolicy
    City -->|contains| CityId
    City -->|contains| State
    City -->|validates with| CityNameSpec
    City -.->|throws| DomainExceptions
    
    %% Infrastructure Persistence Implementation
    RepoAdapter -.->|implements| CityServiceContract
    RepoAdapter -->|maps| EntityMapper
    RepoAdapter -->|uses| Repository
    RepoAdapter -.->|throws| PersistenceExceptions
    Repository -->|persists| Entity
    Entity -->|stores in| Database
    
    %% Configuration Support
    ExecutorConfig -.->|provides executors to| Handler
    ExecutorConfig -.->|provides executors to| CreateCityCommandHandler
    AuditingConfig -.->|audits| Entity
    APIDocConfig -.->|configures| Router
    
    %% Response Path
    Handler -->|Success/Error Response| Router
    Router -->|HTTP Response| Client

    %% Styling
    classDef infrastructure fill:#e1f5ff,stroke:#01579b,stroke-width:2px
    classDef application fill:#fff9c4,stroke:#f57f17,stroke-width:2px
    classDef domain fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px
    classDef external fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef data fill:#ffccbc,stroke:#bf360c,stroke-width:2px
    
    class Router,Handler,RequestDTO,ResponseHelper,Validator,ExceptionHandler,APISpec,RepoAdapter,EntityMapper,Repository,Entity,PersistenceExceptions,ExecutorConfig,AuditingConfig,JacksonConfig,APIDocConfig infrastructure
    class CommandUseCase,QueryUseCase,CreateCityCommandHandler,GetCityQueryHandler,GetAllCityQueryHandler,CreateCityCommand,GetCityQuery,GetAllCityQuery,ServiceContract,CityServiceContract application
    class City,CityId,State,CityPolicy,CityPolicyEnforcer,CityNameSpec,DomainExceptions domain
    class Client external
    class Database data
```

## Layer Descriptions

### 1. External Layer
- **Client/Swagger UI**: External consumers of the API, including Swagger UI for testing and documentation

### 2. Infrastructure Layer - REST
- **CityRouter**: Routes HTTP requests to appropriate handlers using Spring WebFlux functional routing
- **CityHandler**: Handles HTTP requests, validates input, and orchestrates use cases. Maps requests inline to commands/queries
- **Request Objects**:
  - `CityCreateRequest`: Request DTO for creating cities with validation annotations
  - `CityFindAllRequest`: Request DTO for filtering and paginating city queries with support for complex filtering, sorting, and searching
- **ResponseHelper**: Utility class for wrapping responses in SuccessResponseWrapper
- **Validator**: Validates incoming request data
- **Global Exception Handler**: Centralized error handling and response formatting
- **CitySpec**: OpenAPI specification constants for API documentation, including summaries, descriptions, and examples
- **Note**: CityMapper exists in the codebase but is currently unused; mapping is done inline in handlers and use cases

### 3. Application Layer
- **Ports In (Inbound Ports)**:
  - `CommandUseCase`: Interface for command operations (create, update, delete)
  - `QueryUseCase`: Interface for query operations (read)
  
- **Services**:
  - `CreateCityCommandHandler`: Implements business logic for city commands
  - `GetCityQueryHandler`: Implements query logic for retrieving a single city by its unique identifier (UUID)
  - `GetAllCityQueryHandler`: Implements query logic for retrieving multiple cities with pagination, sorting, and search
  
- **Commands & Queries**:
  - `CreateCityCommand`: Command object for creating cities
  - `GetCityQuery`: Query object for retrieving a single city by ID
  - `GetAllCityQuery`: Query object for retrieving multiple cities with pagination parameters
  
- **Ports Out (Outbound Ports)**:
  - `ServiceContract`: Generic interface for persistence operations
  - `CityServiceContract`: City-specific persistence port extending ServiceContract

### 4. Domain Layer (Core Business Logic)
- **Entities**:
  - `City`: Core domain entity with identity, attributes, and business behavior
  
- **Value Objects**:
  - `CityId`: Unique identifier for cities (UUID-based)
  - `State`: Value object representing US state
  
- **Policies**:
  - `CityPolicy`: Interface defining business rules
  - `CityPolicyEnforcer`: Implements business rules like uniqueness validation
  
- **Specifications**:
  - `CityNameNotEmptySpec`: Domain specification for validating city names
  
- **Exceptions**:
  - Domain-specific exceptions for business rule violations

### 5. Infrastructure Layer - Persistence
- **CityRepositoryAdapter**: Adapter implementing `CityServiceContract`, translating domain operations to JPA operations
- **Entity Mapper**: Maps between domain models (`City`) and persistence entities (`CityEntity`)
- **CityRepository**: Spring Data JPA repository interface
- **CityEntity**: JPA entity with database annotations
- **DatabaseException**: Custom exception for database-related errors

### 6. Infrastructure Layer - Configuration
- **Executor Configuration**: Configures thread executors (virtual threads for I/O, platform threads for CPU)
- **Auditing Configuration**: Configures JPA auditing for created/modified timestamps
- **Jackson Configuration**: Configures JSON serialization/deserialization
- **API Documentation Configuration**:
  - `CityGroup`: Configures grouped OpenAPI documentation for City endpoints
  - `GroupedOpenApiProvider`: Interface for creating customized OpenAPI groups with common headers (Authorization, Accept-Language, Currency) and response codes

### 7. Data Store
- **H2 Database**: In-memory database for development and testing

## Data Flow: Create City Example

```mermaid
sequenceDiagram
    participant C as Client
    participant R as CityRouter
    participant H as CityHandler
    participant Req as CityCreateRequest
    participant S as CreateCityCommandHandler
    participant P as CityPolicy
    participant City as City Entity
    participant A as CityRepositoryAdapter
    participant Repo as CityRepository
    participant DB as Database
    participant Helper as ResponseHelper

    C->>R: POST /api/v1/city
    R->>H: createCity(request)
    H->>Req: bodyToMono(CityCreateRequest.class)
    Req-->>H: CityCreateRequest
    H->>H: Validate & Map to CreateCityCommand
    H->>S: create(command, token)
    S->>City: new City(id, name, state)
    City->>City: Validate domain rules
    S->>A: findAll(token)
    A->>Repo: findAll()
    Repo->>DB: SELECT * FROM city
    DB-->>Repo: List<CityEntity>
    Repo-->>A: List<CityEntity>
    A-->>S: List<City>
    S->>P: ensureUnique(city, existing)
    P->>P: Check business rules
    P-->>S: Validation passed
    S->>A: save(city, token)
    A->>A: Map City to CityEntity
    A->>Repo: save(entity)
    Repo->>DB: INSERT INTO city
    DB-->>Repo: CityEntity (with ID)
    Repo-->>A: CityEntity
    A->>A: Map CityEntity to City
    A-->>S: City (saved)
    S->>S: Map City to CityResponse
    S-->>H: CityResponse
    H->>Helper: success(CityResponse)
    Helper-->>H: SuccessResponseWrapper
    H-->>R: ServerResponse
    R-->>C: HTTP 200 OK
```

## GetCity Flow Diagram

The diagram below shows the detailed runtime flow for the "getCity" query (GET /cities/{id}). It illustrates how an incoming HTTP request travels from the REST layer through the application and persistence layers and back to the client, including validation, mapping, and error handling.

```mermaid
sequenceDiagram
    participant Client as Client/Swagger UI
    participant Router as CityRouter
    participant Handler as CityHandler
    participant Validator as Validator
    participant UseCase as GetCityQueryHandler
    participant Persistence as CityServiceContract
    participant Adapter as CityRepositoryAdapter
    participant Repo as CityRepository (JPA)
    participant Entity as CityEntity
    participant Helper as ResponseHelper

    Client->>Router: GET /cities/{id}
    Router->>Handler: route to CityHandler.getCity(id)
    Handler->>Validator: validate(id)
    alt validation fails
        Validator-->>Handler: validation error
        Handler-->>Client: 400 Bad Request / error payload
    else validation passes
        Handler->>Handler: map path param -> GetCityQuery
        Handler->>UseCase: query(GetCityQuery, token)
        UseCase->>UseCase: Extract UUID from query
        UseCase->>Persistence: findByUid(UUID, token)
        Persistence->>Adapter: findByUid(UUID, token)
        Adapter->>Adapter: Convert UUID to String
        Adapter->>Repo: findByUid(String)
        Repo-->>Adapter: Optional<CityEntity>
        Adapter->>Adapter: map CityEntity -> Domain City
        Adapter-->>Persistence: Optional<City>
        UseCase->>UseCase: map City -> CityResponse
        UseCase-->>Handler: CityResponse
        Handler->>Helper: success(CityResponse)
        Helper-->>Handler: SuccessResponseWrapper
        Handler-->>Client: 200 OK + SuccessResponseWrapper
    end

    note right of Handler: Any uncaught exceptions are handled by the Global Exception Handler
```

## GetAllCity Flow Diagram

The diagram below shows the detailed runtime flow for the "getAllCity" query (GET /api/v1/city/all). This endpoint supports pagination, sorting, and searching capabilities. It demonstrates how query parameters are processed, how pagination is handled, and how the response is built and returned to the client.

```mermaid
sequenceDiagram
    participant Client as Client/Swagger UI
    participant Router as CityRouter
    participant Handler as CityHandler
    participant Req as CityFindAllRequest
    participant UseCase as GetAllCityQueryHandler
    participant Persistence as CityServiceContract
    participant Adapter as CityRepositoryAdapter
    participant Repo as CityRepository (JPA)
    participant DB as Database
    participant Helper as ResponseHelper

    Client->>Router: POST /api/v1/city/all
    Router->>Handler: route to CityHandler.getAllCity(request)
    Handler->>Req: bodyToMono(CityFindAllRequest.class)
    Req-->>Handler: CityFindAllRequest (with filters, sort, pagination)
    Handler->>Handler: Extract and validate request parameters
    alt page < 1
        Handler-->>Client: 400 Bad Request (IllegalArgumentException)
    else page valid
        Handler->>Handler: Create GetAllCityQuery from request
        Handler->>UseCase: query(GetAllCityQuery, token)
        Note over UseCase: Executes on virtual thread executor
        UseCase->>Persistence: findAllWithPagination(page, size, search, sort, token)
        Persistence->>Adapter: findAllWithPagination(page, size, search, sort, token)
        Adapter->>Adapter: Parse sort parameter (field, direction)
        Adapter->>Adapter: Create Pageable with Sort
        alt search is empty
            Adapter->>Repo: findAll(pageable)
        else search has value
            Adapter->>Repo: findByNameOrState(search, pageable)
        end
        Repo->>DB: SELECT with pagination, sorting & filtering
        DB-->>Repo: Page<CityEntity>
        Repo-->>Adapter: Page<CityEntity>
        alt totalPages == 0
            Adapter-->>Persistence: Empty List<City>
        else page >= totalPages
            Adapter-->>Handler: IllegalArgumentException (page exceeds total)
            Handler-->>Client: 400 Bad Request
        else valid page
            Adapter->>Adapter: map List<CityEntity> -> List<City>
            Adapter-->>Persistence: List<City>
            Persistence-->>UseCase: List<City>
            UseCase->>UseCase: map List<City> -> List<CityResponse>
            UseCase-->>Handler: List<CityResponse>
            Handler->>Helper: success(List<CityResponse>)
            Helper-->>Handler: SuccessResponseWrapper
            Handler-->>Client: 200 OK + SuccessResponseWrapper
        end
    end

    note right of Handler: Any uncaught exceptions are handled by the Global Exception Handler
    note right of Adapter: Supports case-insensitive search on name and state fields
    note right of Req: CityFindAllRequest supports complex filtering with logical operators (AND/OR)
```

## Key Architectural Principles

### Hexagonal Architecture (Ports & Adapters)
1. **Domain Layer** is at the core and has no dependencies on outer layers
2. **Application Layer** depends only on the domain
3. **Infrastructure Layer** depends on both domain and application layers
4. **Dependency Rule**: Dependencies point inward toward the domain

### Design Patterns Used
1. **Ports and Adapters**: Clear separation between business logic and infrastructure
2. **Command Query Responsibility Segregation (CQRS)**: Separate commands and queries
3. **Repository Pattern**: Abstract data access through repositories
4. **Mapper Pattern**: Transform between layers using dedicated mappers
5. **Specification Pattern**: Encapsulate business rules in reusable specifications
6. **Policy Pattern**: Enforce business policies independently

### Technology Stack
- **Framework**: Spring Boot 4.0.1 with WebFlux (reactive)
- **Database**: Spring Data JPA with H2
- **API Documentation**: SpringDoc OpenAPI 3.0.0
- **Mapping**: MapStruct 1.6.3
- **Build Tool**: Maven
- **Java Version**: JDK 25

### Concurrency Model
- **Virtual Threads**: Used for blocking I/O operations (database calls)
- **CPU Executor**: Used for CPU-intensive operations (business rule validation)
- **Reactive Streams**: WebFlux for non-blocking HTTP handling

## File Organization

```
src/main/java/com/csio/hexagonal/
├── CityServiceApplication.java          # Main application entry point
├── application/                         # Application Layer
│   ├── port/
│   │   ├── in/                          # Inbound ports
│   │   │   ├── CommandUseCase.java
│   │   │   └── QueryUseCase.java
│   │   └── out/                         # Outbound ports
│   │       ├── ServiceContract.java
│   │       └── CityServiceContract.java
│   └── service/
│       ├── command/
│       │   ├── CreateCityCommand.java       # Command object
│       │   └── CreateCityCommandHandler.java # Command handler
│       └── query/
│           ├── GetCityQuery.java            # Query object
│           ├── GetCityQueryHandler.java     # Query handler
│           ├── GetAllCityQuery.java         # Query object
│           └── GetAllCityQueryHandler.java  # Query handler
├── domain/                              # Domain Layer
│   ├── exception/
│   │   ├── DuplicateCityException.java
│   │   ├── InvalidCityNameException.java
│   │   └── InvalidStateNameException.java
│   ├── model/
│   │   └── City.java                    # Domain entity
│   ├── policy/city/
│   │   ├── CityPolicy.java              # Policy interface
│   │   └── CityPolicyEnforcer.java      # Policy implementation
│   ├── specification/
│   │   └── CityNameNotEmptySpec.java    # Domain specification
│   └── vo/
│       ├── CityId.java                  # Value object
│       └── State.java                   # Value object
└── infrastructure/                      # Infrastructure Layer
    ├── config/
    │   ├── executor/
    │   │   ├── AsyncExecutorProperties.java
    │   │   ├── PlatformTaskExecutorConfig.java
    │   │   └── VirtualThreadExecutorConfig.java
    │   ├── AuditingConfig.java
    │   └── JacksonConfig.java
    ├── rest/
    │   ├── exception/
    │   │   ├── ExceptionDetail.java
    │   │   ├── ExceptionMetadataRegistry.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── handler/
    │   │   └── CityHandler.java          # HTTP handler
    │   ├── mapper/
    │   │   └── CityMapper.java           # Mapper (currently unused)
    │   ├── request/
    │   │   ├── CityCreateRequest.java    # Create city request DTO
    │   │   └── CityFindAllRequest.java   # Find all cities request DTO with filtering
    │   ├── response/
    │   │   ├── city/CityResponse.java
    │   │   ├── ResponseInclusion.java
    │   │   ├── helper/
    │   │   │   └── ResponseHelper.java   # Response wrapper utility
    │   │   └── wrapper/
    │   │       ├── ErrorResponseWrapper.java
    │   │       └── SuccessResponseWrapper.java
    │   ├── router/
    │   │   ├── group/
    │   │   │   ├── CityGroup.java        # OpenAPI group configuration
    │   │   │   └── contract/GroupedOpenApiProvider.java  # OpenAPI customization interface
    │   │   └── operation/city/
    │   │       └── CityRouter.java       # Route configuration
    │   ├── spec/
    │   │   └── CitySpec.java             # OpenAPI specification constants
    │   └── validator/
    │       └── Validator.java
    └── store/persistence/
        ├── entity/
        │   ├── AuditableEntity.java
        │   ├── CityEntity.java           # JPA entity
        │   └── contract/Activatable.java
        ├── exception/
        │   └── DatabaseException.java    # Database exception
        ├── mapper/
        │   └── CityMapper.java            # Entity mapper (domain <-> JPA)
        └── adapter/
            ├── CityRepositoryAdapter.java # Persistence adapter
            └── CityRepository.java        # JPA repository
```

## Testing Strategy

The architecture supports multiple testing levels:

1. **Unit Tests**:
   - Domain logic (entities, value objects, policies)
   - Application services (with mocked ports)
   - Mappers and utilities

2. **Integration Tests**:
   - Repository adapters with test database
   - REST handlers with WebTestClient

3. **End-to-End Tests**:
   - Full API testing through HTTP endpoints

## Benefits of This Architecture

1. **Testability**: Each layer can be tested independently
2. **Maintainability**: Clear separation of concerns
3. **Flexibility**: Easy to swap implementations (e.g., change database)
4. **Domain Focus**: Business logic is isolated and protected
5. **Technology Independence**: Domain doesn't depend on frameworks
6. **Scalability**: Reactive stack with proper thread management
