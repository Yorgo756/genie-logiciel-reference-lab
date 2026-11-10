# Eight-stage retrospective

## What worked

- Keeping one verified increment per concern made failures attributable. Lab 2
  and Lab 3 preserved RED tests before their GREEN implementations.
- Executable HTTP tests gave stronger feedback than controller-only inspection:
  they cover success, validation, missing resources, conflicts, ownership, and
  the standardized error body.
- Small Lab 5 refactorings kept behavior stable. Each change was followed by
  the same 17-test regression suite before moving to the next refactoring.
- Quality goals were written before interpreting the generated metrics. The
  first Lab 6 run exposed a real serializability issue that was corrected and
  remeasured.
- The Lab 7 temporary mutation provided direct evidence that a one-day loan
  policy defect is caught by a test.

## Problems encountered and responses

- Spring Initializr exposed Boot 4 metadata and rejected the requested Boot
  3.5.16 archive with HTTP 400. Maven Central was checked, then the Boot 3.5.16
  project and official Maven Wrapper were assembled explicitly.
- The first reservation acceptance tests failed with 404 because no reservation
  route existed. The specified controllers/services/repository were then added,
  and the same tests passed.
- The first Docker base image (`eclipse-temurin:17-jdk-alpine`) lacked a usable
  ARM64 manifest. It was replaced by the Jammy JDK/JRE images; the image built
  and the API was verified in a real container.
- PMD initially reported three `MissingSerialVersionUID` findings. Adding the
  fields reduced PMD to zero without changing behavior. SpotBugs' 18 mutable
  reference warnings remain visible because defensive-copying JPA associations
  or Spring collaborators would be misleading.
- JDK 22 emitted Mockito's future dynamic-agent warning. It is a recorded
  compatibility warning, not a failed Java 17 build.

## What I would improve next

- Obtain the official handout before implementation and reconcile every
  provisional route, status code, and estimation convention.
- Add persistence migrations and a non-memory database profile before any
  production use.
- Add authentication/authorization; current reservation ownership is based
  only on the member identifier in the path.
- Add concurrency tests around simultaneous borrowing/reservation attempts and
  enforce the invariant with database-level constraints where practical.
- Configure a supported Mockito agent for future JDK releases and evaluate a
  narrow, documented SpotBugs filter only after a framework-aware review.

## Evidence integrity

This retrospective refers only to observed commands, reports, commits, and
test output. No time spent, productivity, or student decision absent from the
interaction record is claimed as measured fact.
