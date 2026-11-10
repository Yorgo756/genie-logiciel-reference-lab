# Lab 3 report - specification-driven reservations

Status: specification reviewed; implementation not yet started

## Specification-first evidence

`docs/reservation-specification.md` defines the API paths and acceptance
criteria for HTTP 201, 404, 409, 200, and 204 before any reservation production
class exists. The review chose timestamped cancellation, owner-scoped 404
responses, active-only duplicate prevention, and reservations only for books
that are currently unavailable.

Implementation and actual verification results will be appended after the
specification commit.
