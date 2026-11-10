# Lab 1 report — scaffold and verify

Status: verified

## Objective

Create the Java 17-compatible Spring Boot 3 Library Management REST API with
Book, Member, and Borrowing layers, seeded H2 data, CRUD HTTP endpoints, and
automated verification.

## Environment baseline

- Branch: `yorgo/reference-lab`
- Java runtime/compiler observed: Oracle 22.0.2
- Maven: not installed globally; Maven Wrapper selected
- Docker CLI: detected; daemon verification deferred to Lab 4
- Spring Initializr Boot 3 request: actual HTTP 400
- Spring Boot version selected from Maven Central: 3.5.16

## Implementation and verification

### Implemented files and behavior

- `pom.xml` pins Spring Boot 3.5.16 and Java 17 release compatibility.
- Maven Wrapper launchers and wrapper configuration remove the need for a
  globally installed Maven.
- JPA entities: `Book`, `Member`, and `Borrowing`.
- Spring Data repositories and explicit service/controller layers for all three
  resources.
- CRUD mappings under `/api/books`, `/api/members`, and `/api/borrowings`.
- H2 in-memory configuration and startup seeding of exactly five books and
  three members.
- JUnit/Spring Boot/MockMvc integration tests plus a Mockito unit test.

### Commands and actual results

1. `./mvnw test` — first run: **BUILD FAILURE**. Seven tests ran; one
   assertion failed and one test errored.
2. Review found that HTTP test methods shared committed H2 mutations and that
   JsonPath returned a small numeric ID as `Integer`, which the test directly
   cast to `Long`.
3. Added transactional test rollback and converted the parsed value through
   `Number.longValue()`.
4. `./mvnw test` — second run: **BUILD SUCCESS**. Seven tests ran with zero
   failures, zero errors, and zero skipped. Maven reported compilation with
   `javac ... release 17` while executing on JDK 22.0.2.
5. `./mvnw spring-boot:run` — application started on port 8080.
6. Initial sandboxed `curl` calls returned connection refused because the app
   and client were in isolated execution contexts. Repeating the calls in the
   same context produced the real results below.

### Live HTTP excerpts

```text
GET /api/books       -> HTTP 200, 5 JSON objects
GET /api/members     -> HTTP 200, 3 JSON objects
POST /api/borrowings -> HTTP 201, borrowing id 1
GET /api/borrowings  -> HTTP 200, 1 JSON object
```

The POST request used book 1, member 1, borrowed date `2026-11-10`, and due
date `2026-11-24`. The application was stopped gracefully after verification.

### Review decisions

- Direct entity request/response bodies are accepted provisionally in Lab 1;
  DTO and mapper separation is deliberately reserved for Lab 4.
- `ResponseStatusException` is a simple provisional 404 mechanism; consistent
  problem details and centralized handling are reserved for Lab 4.
- Lab 1 borrowing CRUD records dates and relationships but intentionally does
  not yet implement checkout, return, availability, or late-fee rules. Those
  behaviors belong to the Lab 2 user stories and must be added through RED/GREEN
  commits.
- The H2 database is intentionally ephemeral. Seed initialization is guarded by
  repository counts so it does not duplicate data within one application run.

### Known warnings and limitations

- Mockito emitted a JDK 22 dynamic-agent warning. It did not fail the build;
  it should be revisited during the quality/configuration stages.
- Docker was not required for Lab 1 and has not yet been daemon-tested.
- The missing official handout means endpoint shapes remain provisional; see
  `provisional-assumptions.md`.

### Commits

- `7748938` — `lab1: scaffold library management REST API`
- The documentation-only commit that records this hash is intentionally not
  self-referenced, avoiding a circular commit identifier.
