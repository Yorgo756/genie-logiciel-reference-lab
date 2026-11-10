# Agentic Development Reference Lab

This directory contains an individual Java 17-compatible Spring Boot 3 Library
Management REST API developed through eight progressive lab stages.

## Run locally

```bash
./mvnw test
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080/api`. The development-only H2
console is available at `http://localhost:8080/h2-console` using JDBC URL
`jdbc:h2:mem:library`, user `sa`, and a blank password.

Evidence and stage reports are stored in [`reports/`](reports/).

## Quality and container verification

```bash
./mvnw clean verify pmd:pmd spotbugs:spotbugs
docker build -t library-api:final .
docker run --rm -p 8080:8080 library-api:final
```

OpenAPI JSON is published at `/v3/api-docs` and Swagger UI at
`/swagger-ui/index.html`. The final measured results and live-container HTTP
checks are recorded in [`reports/final-verification.md`](reports/final-verification.md).
