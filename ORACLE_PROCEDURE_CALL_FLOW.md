# Oracle Procedure Call Flow (Current Branch)

This document explains how the CORPIB stored procedure `dpr_src_act_info` is wired end-to-end in this branch using the default Spring Boot datasource.

## High-level flow (default datasource)

```mermaid
sequenceDiagram
    participant Client as Client/Postman/Swagger
    participant Router as CorpibRouter
    participant Handler as CorpibProcedureHandler
    participant UseCase as DprSrcActInfoQueryHandler
    participant Port as OracleProcedurePort
    participant Adapter as OracleJpaProcedureAdapter
    participant Caller as AbstractStoredProcedureCaller
    participant JPA as JPA EntityManager
    participant SP as CORPIB.dpr_src_act_info

    Client->>Router: POST /api/v1/corpib/act-info
    Router->>Handler: dprSrcActInfo(request)
    Handler->>UseCase: query(DprSrcActInfoQuery)
    UseCase->>Port: dprSrcActInfo(query)
    Port->>Adapter: dprSrcActInfo(query)
    Adapter->>Caller: executeProcedure(...)
    Caller->>JPA: createStoredProcedureQuery(...)
    JPA->>SP: call procedure
    SP-->>JPA: OUT params (actType, actName, actBal, status, code, message)
    JPA-->>Adapter: outputs map
    Adapter-->>UseCase: DprSrcActInfoResponse
    UseCase-->>Handler: DprSrcActInfoResponse
    Handler-->>Client: SuccessResponseWrapper
```

## Procedure call details

Stored procedure signature:

```
PROCEDURE dpr_src_act_info (
    in_user_code  IN  VARCHAR2,
    in_org_code   IN  VARCHAR2,
    in_actnum     IN  VARCHAR2,
    out_acttype   OUT VARCHAR2,
    out_actname   OUT VARCHAR2,
    out_actbal    OUT NUMBER,
    out_status    OUT VARCHAR2,
    out_code      OUT INTEGER,
    out_message   OUT VARCHAR2
)
```

Procedure parameter metadata:
- `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/procedure/DprSrcActInfoParam.java`

Procedure execution (named parameters with a single adapter):
- `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/adapter/OracleJpaProcedureAdapter.java`
- `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/procedure/AbstractStoredProcedureCaller.java`

## REST usage

Endpoint:
- `POST /api/v1/corpib/act-info`

Request body (full fields):
```json
{
  "userCode": "USER001",
  "orgCode": "ORG001",
  "actNum": "08533000197"
}
```

Notes:
- `actNumber` is also accepted via `@JsonAlias` and mapped to `actNum`.

## Datasource configuration (current branch)

Default datasource configured in `src/main/resources/application.properties`:
```
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.url=jdbc:oracle:thin:@//192.183.155.12:1535/stlbas
spring.datasource.username=CORPIB
spring.datasource.password=CORPIB
```

There is no datasource routing in this branch; Spring Boot auto-configures JPA from the default datasource.

## Key files (current branch)

- REST entry + OpenAPI:
  - `src/main/java/com/csio/hexagonal/infrastructure/rest/router/operation/corpib/CorpibRouter.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/rest/handler/CorpibProcedureHandler.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/rest/spec/CorpibSpec.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/rest/request/DprSrcActInfoRequest.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/rest/response/corpib/DprSrcActInfoResponse.java`
- Application layer:
  - `src/main/java/com/csio/hexagonal/application/service/query/DprSrcActInfoQueryHandler.java`
  - `src/main/java/com/csio/hexagonal/application/service/query/DprSrcActInfoQuery.java`
  - `src/main/java/com/csio/hexagonal/application/port/out/OracleProcedurePort.java`
- Stored procedure execution:
  - `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/adapter/OracleJpaProcedureAdapter.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/procedure/AbstractStoredProcedureCaller.java`
  - `src/main/java/com/csio/hexagonal/infrastructure/store/persistence/procedure/DprSrcActInfoParam.java`

## Summary

This branch keeps stored procedure logic inside the infrastructure adapter, uses the outbound port to isolate database details, and relies on the default Spring Boot datasource configured in `application.properties`.
