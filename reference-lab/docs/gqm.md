# Goal-Question-Metric quality plan

This GQM plan is provisional because the official handout supplied no numeric
quality thresholds. Targets are goals, not claimed measurements.

## Goal 1 - preserve correct library workflows

Question: Do automated tests cover the implemented success and error workflows?

Metrics:

- Test pass/fail/error/skip counts from Maven Surefire.
- JaCoCo instruction and branch coverage for application classes.
- Provisional targets: 100% test pass rate, at least 70% instruction coverage,
  and at least 60% branch coverage. A missed target is reported, not hidden.

## Goal 2 - reduce detectable implementation defects

Question: What actionable static-analysis findings remain?

Metrics:

- PMD violations by rule and priority.
- SpotBugs bug instances by category and rank.
- Correct at least one genuine finding when a safe correction exists, then
  rerun tests and analyzers.

## Goal 3 - keep HTTP behavior understandable

Question: Are routes documented and failures consistent?

Metrics:

- OpenAPI document generation and documented path count.
- HTTP tests for 201, 204, 400, 404, and 409 responses.
- Presence of the standard timestamp/status/error/message/path error shape.

## Measurement integrity

Only generated reports and command outputs are recorded as actual values. No
hours, coverage percentages, or finding counts are estimated and presented as
measurements.
