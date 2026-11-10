# Provisional function-point and effort estimate

This is a hypothetical classroom estimation exercise, not a measured project
size or a record of development hours. The official handout is unavailable,
so the boundary and complexity assumptions below are explicit.

## Boundary and counting assumptions

The application boundary is the Library Management REST API. H2 is internal
storage. Swagger UI, build tooling, tests, and seeded data are technical
support and are not counted as user functions. Each implemented REST
transaction is classified once. All transactions and logical files use the
IFPUG average-complexity weights because data-element and record-element
thresholds were not specified.

## Unadjusted function-point count

| Type | Implemented functions counted | Count | Average weight | Points |
| --- | --- | ---: | ---: | ---: |
| External input (EI) | Create/update/delete books; create/update/delete members; create/update/delete generic borrowings; member borrow; return; create/cancel reservation | 13 | 4 | 52 |
| External output (EO) | Calculated late-fee response | 1 | 5 | 5 |
| External inquiry (EQ) | List/get books; list/get members; list/get borrowings; list member active loans; list member reservations | 8 | 4 | 32 |
| Internal logical file (ILF) | Books, members, borrowings, reservations | 4 | 10 | 40 |
| External interface file (EIF) | None | 0 | 7 | 0 |
| **Total UFP** |  |  |  | **129** |

No value-adjustment factor is applied. The estimate is therefore 129
unadjusted function points under these assumptions. A formal IFPUG review
could reclassify or combine transactions and change this number.

## Hypothetical effort scenarios

The formula is `person-months = 129 UFP / assumed productivity`. These are
planning scenarios only; they are not actual effort and are not converted to
hours.

| Assumed productivity | Hypothetical effort |
| ---: | ---: |
| 8 FP/person-month | 16.13 person-months |
| 12 FP/person-month | 10.75 person-months |
| 16 FP/person-month | 8.06 person-months |

The estimates describe a conventional delivery context and should not be
interpreted as the time taken by this reference-lab exercise.

## Actual-effort statement

No start/stop time log or independently measured development-hours record was
kept. Consequently, this submission reports no actual hours or measured
productivity. Git commits, test counts, coverage, and command results are real
engineering evidence, but they are not substitutes for elapsed-time data.
