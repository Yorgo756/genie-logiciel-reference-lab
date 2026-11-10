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
