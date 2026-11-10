# Lab 6 report - static analysis and coverage

Status: complete

## Configuration

- Maven PMD Plugin 3.28.0 with best-practices and error-prone rules.
- SpotBugs Maven Plugin 4.10.5.0 at maximum effort and low threshold.
- JaCoCo Maven Plugin 0.8.16 attached to tests with report generation during
  `verify`.

These versions were selected from actual Maven Central metadata on 2026-11-10.
Analyzer findings and coverage below were read from generated reports.

## First measurement

Command:

```text
./mvnw clean verify pmd:pmd spotbugs:spotbugs
```

Actual result: `BUILD SUCCESS`; 17 tests passed with no failures, errors, or
skips. JaCoCo measured 1,205 of 1,486 instructions (81.09%) and 22 of 32
branches (68.75%). Both exceeded the provisional GQM targets.

PMD reported three priority-3 `MissingSerialVersionUID` violations, one in
each custom exception: `ConflictException`, `InvalidRequestException`, and
`ResourceNotFoundException`.

SpotBugs reported 18 priority-2 instances:

- 4 `EI_EXPOSE_REP` warnings.
- 14 `EI_EXPOSE_REP2` warnings.

They concern mutable JPA entity references and Spring-injected collaborators.
Changing entity associations to defensive copies would conflict with JPA
identity and dirty tracking, while copying injected services would be
meaningless. The warnings are retained and reported rather than hidden with a
filter.

## Genuine correction and second measurement

Added `private static final long serialVersionUID = 1L;` to all three custom
exceptions, then reran the same command.

Actual result: `BUILD SUCCESS` in 14.533 s. All 17 tests passed again. PMD
violations fell from 3 to 0. SpotBugs remained at 18 (4 `EI_EXPOSE_REP`, 14
`EI_EXPOSE_REP2`). Coverage was unchanged at 81.09% instruction and 68.75%
branch.

Generated evidence:

- `target/pmd.xml`
- `target/spotbugsXml.xml`
- `target/site/jacoco/jacoco.csv`
- `target/site/jacoco/index.html`
- `target/surefire-reports/`

The `target/` directory is intentionally ignored by Git because these reports
are reproducible build output; this report preserves the measured values and
the exact command.

## Review

- Quality goals are explicitly provisional because the official handout is
  unavailable.
- The PMD correction was small, behavior-preserving, and regression-tested.
- Remaining SpotBugs warnings are not claimed as fixed or suppressed.
- The JDK 22 test run emitted Mockito's future dynamic-agent warning; it did
  not fail this Java 17-compatible build.

Quality-tooling and correction commit: `9c384f3`.
