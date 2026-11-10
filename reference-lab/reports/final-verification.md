# Final verification report

Date: 2026-11-10 (Asia/Beirut)

## Scope and environment

- Branch: `yorgo/reference-lab`
- Remote: `https://github.com/Yorgo756/genie-logiciel-reference-lab.git`
- Host runtime: Oracle JDK 22.0.2; Maven compiles with Java release 17.
- Maven Wrapper runtime: Apache Maven 3.10.0.
- Git: 2.50.1 (Apple Git-155).
- Docker client/server: 29.9.3 on ARM64 macOS.
- Container runtime: Eclipse Temurin OpenJDK 17.0.21.

## Clean build and quality gate

Command:

```text
./mvnw clean verify pmd:pmd spotbugs:spotbugs
```

Actual result: `BUILD SUCCESS` in 14.152 s.

- Tests: 21 run, 0 failures, 0 errors, 0 skipped.
- JaCoCo instructions: 1,219 / 1,486 = 82.03%.
- JaCoCo branches: 23 / 32 = 71.88%.
- PMD: 0 violations.
- SpotBugs: 18 disclosed priority-2 findings (4 `EI_EXPOSE_REP`, 14
  `EI_EXPOSE_REP2`); none are hidden or claimed as fixed.

The complete terminal output is preserved in
`reports/evidence/final-verification.txt`.

## Docker build and live HTTP verification

`docker build -t library-api:final .` completed successfully. The exported
manifest-list digest was
`sha256:1ba0a7d97e2f8fffd2ddd1f98a131930b8b163977481faadf45ef8438ce9fdac`.
The image was run as `library-api-final` with host port 18081 mapped to 8080.

Observed live responses:

| Check | Actual result |
| --- | --- |
| Seeded books | 200; JSON array length 5 |
| OpenAPI | 200; title `Library Management API`; 11 paths |
| Swagger UI | 200 |
| Invalid book | 400; standard `ApiError` path `/api/books` |
| Borrow book | 201; due 2026-11-24; book unavailable |
| Reserve borrowed book | 201; owner member 2 |
| List owner reservations | 200; one active reservation |
| Cancel reservation | 204 |
| Return book | 200; book available |
| Return same loan again | 409; `Borrowing has already been returned` |

The compact command output is preserved in
`reports/evidence/final-docker-http.txt`. The temporary container was stopped
and removed after verification; the `library-api:final` image was retained.

## Remaining limitations

- The official lab handout was unavailable; routes, thresholds, and the
  function-point convention remain explicitly provisional.
- H2 is in-memory and authentication/authorization is outside the provisional
  scope.
- SpotBugs' framework/JPA mutable-reference warnings remain disclosed.
- Mockito reports a future JDK dynamic-agent compatibility warning on host JDK
  22; it did not fail the Java 17-compatible build.
- No measured development-hours record exists, so no actual effort is claimed.
