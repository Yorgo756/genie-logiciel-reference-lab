# Lab 4 report - design, OpenAPI, and deployment

Status: verified

## Design changes

- Request/response records are organized under `dto` and JPA entities no longer
  cross the controller response boundary.
- Dedicated mappers handle entity/DTO conversion.
- Services throw domain exceptions rather than HTTP framework exceptions.
- `GlobalExceptionHandler` returns one timestamp/status/error/message/path shape
  for 400, 404, and 409 responses.
- Springdoc OpenAPI 2.8.17 was selected after Maven Central was queried and
  confirmed that version was available.

## Verification so far

`./mvnw test` after the boundary redesign: 15 tests, zero failures/errors,
BUILD SUCCESS.

After adding explicit OpenAPI/Swagger tests, `./mvnw test` ran 16 tests with
zero failures/errors and BUILD SUCCESS.

## Docker evidence

Docker client and server both reported version 29.9.3.

The first `docker build -t library-api:lab4 .` failed because
`eclipse-temurin:17-jdk-alpine` had no matching manifest for the ARM64 Docker
platform. The Dockerfile was corrected to the multi-platform Jammy JDK/JRE
images. The second build completed successfully and produced image
`library-api:lab4`.

Container startup reported Spring Boot 3.5.16 running on Java 17.0.21. The
temporary container mapped host port 18080 to application port 8080. Actual
checks were:

```text
GET /api/books              -> HTTP 200, 5 seeded books
GET /v3/api-docs            -> HTTP 200, title "Library Management API"
OpenAPI documented paths    -> 11
GET /swagger-ui/index.html  -> HTTP 200
POST invalid /api/books     -> HTTP 400 with ApiError fields
```

The temporary container was removed after verification. The local image was
retained for later final verification.

## Review

The controllers now own HTTP mapping and DTO conversion while services own
business behavior. Entity relationships no longer define external JSON. The
global handler deliberately does not expose database exception details. The H2
console and Swagger UI remain enabled because this is a reference/development
application, not a production deployment.
