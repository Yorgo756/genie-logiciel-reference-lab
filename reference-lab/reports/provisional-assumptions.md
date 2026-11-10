# Provisional assumptions

The official handout was unavailable when implementation began on 2026-11-10.
The following assumptions are therefore explicit and may be revised when the
handout becomes available:

- The eight stages supplied by the student are the working specification.
- REST resources use the `/api/books`, `/api/members`, `/api/borrowings`, and
  later `/api/reservations` paths.
- Numeric database-generated identifiers are acceptable.
- A loan lasts exactly 14 calendar days and overdue charges use whole late days.
- H2 is an in-memory development and test database; seeded data is recreated on
  each application start.
- Spring Boot 3.5.16 is used because Maven Central confirmed it is available;
  current Spring Initializr supports Boot 4 only.
- Java is compiled with release/source compatibility 17 while the local runtime
  is Oracle JDK 22.0.2.
- Authentication and authorization are outside the provisional scope. An owner
  in reservation requirements means the member identified by the request/path.
- Recorded command outputs and measurements must come from actual executions.
