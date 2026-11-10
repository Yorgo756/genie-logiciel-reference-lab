# Lab 2 sprint backlog

This backlog is provisional because the official course handout was unavailable.
Stories are ordered to establish the loan lifecycle before reporting and fees.

## Definition of done

- Acceptance behavior is expressed by an automated test before implementation.
- The RED run is captured truthfully, followed by a GREEN run of the complete suite.
- HTTP status and persisted state are both verified where relevant.
- No active loan can be duplicated and no completed loan can be returned twice.

## Story 1 - Borrow a book for 14 days

As an active library member, I want to borrow an available book so that I can
use it for 14 calendar days.

Acceptance criteria:

- A valid request creates a borrowing with `dueDate = borrowedOn + 14 days`.
- The book becomes unavailable and the API returns HTTP 201.
- Missing book/member returns 404; inactive member, unavailable book, or a
  duplicate active borrowing returns 409.

## Story 2 - Return a book

As a borrower, I want to return an active loan so that the book becomes
available again.

Acceptance criteria:

- Returning an active loan records the return date and restores availability.
- A second return or a return date before the borrowing date returns 409.
- A missing borrowing returns 404.

## Story 3 - List a member's active loans

As a library member, I want to list my active loans so that I know what I still
need to return.

Acceptance criteria:

- The response includes only loans with no return date for that member.
- A missing member returns 404.

## Story 4 - Calculate overdue charge

As library staff, I want an overdue charge calculated at EUR 0.50 per whole
late day so that fees are consistent.

Acceptance criteria:

- A loan due four days earlier produces `EUR 2.00`.
- A loan returned/on-time or evaluated before its due date produces `EUR 0.00`.
- A completed loan is charged only through its recorded return date.
