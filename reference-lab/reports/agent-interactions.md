# Genuine agent interaction record

## 2026-11-10 — environment and Lab 1 kickoff

**Student request.** Build an individual eight-stage Agentic Development
Reference Lab under `reference-lab/`, starting with a Spring Boot Library API,
using real tests, reviews, commits, and evidence without inventing results.

**Agent actions and decisions.**

1. Verified the clean `yorgo/reference-lab` branch and personal GitHub remote.
2. Detected Oracle JDK 22.0.2, no global Maven, and a Docker CLI.
3. Queried official Spring Initializr metadata. It offered Spring Boot 4 only.
4. Queried Maven Central and confirmed Spring Boot 3.5.16 was available.
5. Attempted to request a Boot 3.5.16 Initializr archive; the service returned
   HTTP 400. The scaffold was therefore created explicitly with Java 17 release
   compatibility and an official Maven Wrapper.

This is a concise evidence record of the real interaction. It does not claim to
be a verbatim export of hidden reasoning or tool internals.

## 2026-11-10 - Labs 2 to 4

**Student request.** Continue through test-driven lending stories,
specification-driven reservations, design improvements, OpenAPI, and a verified
Docker deployment.

**Agent actions and observed problems.**

1. Added lending tests first; four tests genuinely failed with 404 before the
   routes were implemented, then passed after the GREEN implementation.
2. Committed reservation acceptance criteria before code. Three acceptance
   tests then failed with 404 and passed after implementation.
3. Introduced DTOs, mappers, domain exceptions, and a consistent `ApiError`
   response, then verified OpenAPI and Swagger endpoints.
4. The Alpine Temurin image failed on ARM64. Jammy JDK/JRE images built and ran;
   live container requests returned five books, OpenAPI data, Swagger UI, and a
   400 validation response.

## 2026-11-10 - Labs 5 to 7

**Student request.** Perform three controlled refactorings, measure quality,
expand tests, and prove that tests detect a deliberate defect.

**Agent actions and observed results.**

1. Extracted `LoanPolicy`, encapsulated domain state transitions, and simplified
   the active-loan endpoint. The 17-test suite passed after each refactoring.
2. Ran PMD, SpotBugs, and JaCoCo. The first run measured 81.09% instruction and
   68.75% branch coverage; PMD reported three serial-version findings and
   SpotBugs reported 18 exposed-reference warnings.
3. Corrected the three PMD findings and reran the tools: PMD reached zero while
   coverage and the 18 disclosed SpotBugs warnings remained.
4. Expanded the suite to 21 tests. A temporary 13-day loan defect caused the
   targeted 14-day test to fail with the exact one-day mismatch. Restoring 14
   days returned the full suite to green; both raw logs were committed.

## 2026-11-10 - Lab 8

**Student request.** Produce function-point and effort estimates while never
inventing actual hours or results, then reflect on the workflow.

**Agent decision.** Counted an explicitly provisional 129 UFP using visible
average-weight assumptions and presented productivity scenarios only as
hypothetical person-month estimates. Because no time log exists, no actual
hours or measured productivity are claimed.
