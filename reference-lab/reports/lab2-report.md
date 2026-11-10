# Lab 2 report - Agile and test-driven lending

Status: GREEN and regression verified

## Plan

The sprint backlog defines four user stories and a shared definition of done.
HTTP integration tests are written before the routes and rules they specify.
Actual RED/GREEN commands, results, corrections, and commit identifiers will be
recorded as they occur.

## RED evidence

Command:

```text
./mvnw -Dtest=BorrowingWorkflowIntegrationTest test
```

Actual result: **BUILD FAILURE**. Four tests ran, four failed, zero errored,
zero skipped. Each first borrowing request expected HTTP 201 but received HTTP
404 because `/api/members/{memberId}/borrowings` did not yet exist. This confirms
the tests preceded the implementation rather than passing against Lab 1 code.

RED commit: `4f0532e` (`test(lab2): define lending stories and capture red tests`).

## GREEN implementation and evidence

- Added a transactional 14-day borrowing operation that marks the book
  unavailable and rejects inactive members, unavailable books, and duplicate
  active loans with HTTP 409.
- Added return behavior that restores availability and rejects a second return
  or a return before the borrowing date.
- Added the active-member-loan query.
- Added EUR late-fee calculation using `max(0, whole late days) * 0.50`; a
  completed loan stops accruing at its return date.

Commands and actual results:

```text
./mvnw -Dtest=BorrowingWorkflowIntegrationTest test
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS

./mvnw test
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS

./mvnw test  # after explicit inactive-member and invalid-return tests
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS
```

The JDK 22 Mockito dynamic-agent warning remained non-fatal. No production
failure was hidden or waived.

## Review

The original Lab 1 generic borrowing POST now delegates to the same 14-day
business operation so it cannot bypass availability/member checks. DTO cleanup
and removal of HTTP-specific exceptions from services remain scheduled for Lab 4.

## Commits

- `4f0532e` - RED backlog and failing HTTP tests
- `801f5bd` - GREEN lending lifecycle and regression evidence
