# Lab 5 report - incremental refactoring

Status: refactoring 1 in progress

## Baseline

The pre-refactoring Lab 4 suite contained 16 passing tests. Refactorings are
kept behavior-preserving, small, separately committed, and followed by the full
regression suite.

## Refactoring 1 - extract loan policy

Before: `BorrowingService` combined transaction orchestration with the magic
values `14` and `0.50` and with date/fee arithmetic.

After: `LoanPolicy` owns loan duration and fee calculation. `BorrowingService`
delegates those decisions and remains responsible for persistence and workflow.

Regression command/result: `./mvnw test` - 17 tests, zero failures/errors,
BUILD SUCCESS.

Commit: `baef2ee`.

## Refactoring 2 - express lifecycle transitions in the domain

Before: `BorrowingService` directly toggled `Book.available` and inspected and
assigned `Borrowing.returnedOn`, spreading the meaning of a completed loan.

After: `Book.markBorrowed()`, `Book.markReturned()`, `Borrowing.isActive()`, and
`Borrowing.completeOn()` name and group the state transitions. The service keeps
validation and transaction orchestration.

Regression command/result: `./mvnw test` - 17 tests, zero failures/errors,
BUILD SUCCESS.
