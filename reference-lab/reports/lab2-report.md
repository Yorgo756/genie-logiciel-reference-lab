# Lab 2 report - Agile and test-driven lending

Status: RED iteration captured

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
