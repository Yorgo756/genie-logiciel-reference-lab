# Lab 7 report - test quality and mutation demonstration

Status: in progress

## Added coverage

`ErrorWorkflowIntegrationTest` adds end-to-end HTTP checks for:

- the complete `ApiError` shape on a missing book (404);
- multiple invalid member fields (400);
- missing members and books in the borrowing workflow (404);
- stopping a completed loan's late-fee calculation at its return date.

Existing workflow tests already cover duplicate active borrowing, invalid
return states, inactive members, reservation conflicts, reservation ownership,
and successful create/list/cancel flows.

## Deliberate-defect protocol

After the expanded suite is green, temporarily change the 14-day loan policy
to 13 days without committing the defect. Run the exact test that asserts the
14-day due date, retain the genuine failing output, restore the implementation,
and rerun the full suite. No broken implementation will remain in Git history.
