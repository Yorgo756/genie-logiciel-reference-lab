# Lab 7 report - test quality and mutation demonstration

Status: complete

## Added coverage

`ErrorWorkflowIntegrationTest` adds end-to-end HTTP checks for:

- the complete `ApiError` shape on a missing book (404);
- multiple invalid member fields (400);
- missing members and books in the borrowing workflow (404);
- stopping a completed loan's late-fee calculation at its return date.

Existing workflow tests already cover duplicate active borrowing, invalid
return states, inactive members, reservation conflicts, reservation ownership,
and successful create/list/cancel flows.

The expanded baseline command `./mvnw test` produced `BUILD SUCCESS`: 21
tests, 0 failures, 0 errors, 0 skipped. The baseline test commit is `1bbb2a1`.

## Deliberate-defect protocol

After the expanded suite is green, temporarily change the 14-day loan policy
to 13 days without committing the defect. Run the exact test that asserts the
14-day due date, retain the genuine failing output, restore the implementation,
and rerun the full suite. No broken implementation remains in Git history.

## Actual mutation result

The temporary edit changed `LoanPolicy.LOAN_DAYS` from 14 to 13. The targeted
command was:

```text
./mvnw -Dtest=BorrowingWorkflowIntegrationTest#borrowsForFourteenDaysAndRejectsDuplicateActiveBorrowing test
```

Actual outcome: `BUILD FAILURE`; 1 test run, 1 failure. The decisive assertion
was:

```text
JSON path "$.dueDate" expected:<2026-11-24> but was:<2026-11-23>
```

This demonstrates that the workflow test detects a one-day policy regression,
not merely that the application starts. The full unedited terminal output is
preserved in `reports/evidence/lab7-deliberate-defect.txt`.

## Restoration and regression result

The constant was immediately restored to 14 and `./mvnw test` was rerun.
Actual outcome: `BUILD SUCCESS`; 21 tests, 0 failures, 0 errors, 0 skipped.
The full output is preserved in
`reports/evidence/lab7-restored-suite.txt`.

Review confirmed that `LoanPolicy.java` has no working-tree difference from
the committed correct implementation. The failure/recovery evidence commit is
`365dd7b`.
