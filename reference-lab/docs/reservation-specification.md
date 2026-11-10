# Reservation specification

Status: reviewed provisional specification, written before implementation

The official handout was unavailable. These criteria interpret "reservations
for unavailable books" as a wait-list action: an available book must be borrowed
directly and cannot be reserved.

## API contract

### Create a reservation

`POST /api/members/{memberId}/reservations`

Request body:

```json
{"bookId": 1, "reservedOn": "2026-11-10"}
```

Acceptance criteria:

- Given an active member and an unavailable book, creation returns HTTP 201,
  a `Location` header, and an active reservation owned by that member.
- A missing member or book returns HTTP 404.
- An inactive member returns HTTP 409.
- An available book returns HTTP 409 because it can be borrowed directly.
- A second active reservation for the same member and book returns HTTP 409.

### List a member's reservations

`GET /api/members/{memberId}/reservations`

Acceptance criteria:

- Returns HTTP 200 and only that member's active reservations.
- A missing member returns HTTP 404.

### Cancel an owned reservation

`DELETE /api/members/{memberId}/reservations/{reservationId}`

Acceptance criteria:

- The owner can cancel an active reservation and receives HTTP 204.
- The cancelled reservation no longer appears in the active list.
- A missing reservation, already-cancelled reservation, or reservation owned by
  another member returns HTTP 404. The owner-specific 404 avoids exposing
  another member's reservation details.

## Review decisions

- Cancellation is retained as a timestamped state transition rather than a
  physical delete, preserving an audit trail.
- Duplicate prevention applies only to active reservations; a member may make a
  later reservation after cancellation.
- Queue ordering and automatic fulfilment are not specified and are outside
  this provisional stage.
