# Lab 3 report - specification-driven reservations

Status: implementation and regression verified

## Specification-first evidence

`docs/reservation-specification.md` defines the API paths and acceptance
criteria for HTTP 201, 404, 409, 200, and 204 before any reservation production
class exists. The review chose timestamped cancellation, owner-scoped 404
responses, active-only duplicate prevention, and reservations only for books
that are currently unavailable.

Implementation and actual verification results will be appended after the
specification commit.

Specification commit: `442514a`.

## Pre-implementation acceptance run

Command: `./mvnw -Dtest=ReservationWorkflowIntegrationTest test`

Actual result: **BUILD FAILURE**. Three tests ran and all three failed with
expected reservation status codes receiving HTTP 404. Their prerequisite Lab 2
borrowing calls returned HTTP 201, isolating the failure to the deliberately
missing reservation routes.

Acceptance-test commit: `a0abf14`.

## Implementation

- Added `Reservation` with retained `reservedOn` and `cancelledOn` dates.
- Added active-only duplicate and owner-scoped repository queries.
- Enforced active membership and unavailable-book preconditions.
- Added member-specific create/list/cancel endpoints with 201/200/204 success
  codes and 404/409 error behavior matching the specification.

## Verification

```text
./mvnw -Dtest=ReservationWorkflowIntegrationTest test
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS

./mvnw test
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 - BUILD SUCCESS
```

The tests verified creation, duplicate conflict, available-book conflict,
inactive-member conflict, missing resources, owner-only cancellation, repeat
cancellation 404, and active-list removal after cancellation.

## Review

The implementation follows the written specification without adding automatic
queue fulfilment. `LocalDate.now()` is currently used for the cancellation audit
date; a clock abstraction was considered unnecessary for the specified response
behavior but remains a possible refactoring. HTTP-specific exceptions will be
removed from services in Lab 4.

## Commits

- `442514a` - reviewed specification before implementation
- `a0abf14` - acceptance tests and captured pre-implementation failures
- `8192839` - verified reservation implementation
