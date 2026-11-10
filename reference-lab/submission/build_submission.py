from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfbase import pdfmetrics
from reportlab.platypus import (
    HRFlowable,
    KeepTogether,
    PageBreak,
    Paragraph,
    Preformatted,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


OUTPUT = Path(__file__).resolve().parent / "agentic-development-reference-lab.pdf"

NAVY = colors.HexColor("#0B1F3A")
BLUE = colors.HexColor("#2563EB")
TEAL = colors.HexColor("#0F766E")
INK = colors.HexColor("#172033")
MUTED = colors.HexColor("#5B6474")
PALE = colors.HexColor("#F3F6FA")
LINE = colors.HexColor("#D7DEE9")
GREEN = colors.HexColor("#DDF6E9")
AMBER = colors.HexColor("#FFF3D6")
WHITE = colors.white

PAGE_W, PAGE_H = A4
LEFT = 18 * mm
RIGHT = 18 * mm
TOP = 19 * mm
BOTTOM = 17 * mm
CONTENT_W = PAGE_W - LEFT - RIGHT


styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="CoverTitle", parent=styles["Title"], fontName="Helvetica-Bold",
    fontSize=28, leading=33, textColor=WHITE, alignment=TA_LEFT,
    spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="CoverSub", parent=styles["Normal"], fontName="Helvetica",
    fontSize=13, leading=18, textColor=colors.HexColor("#DCE8FF"),
))
styles.add(ParagraphStyle(
    name="H1x", parent=styles["Heading1"], fontName="Helvetica-Bold",
    fontSize=20, leading=24, textColor=NAVY, spaceAfter=9,
))
styles.add(ParagraphStyle(
    name="H2x", parent=styles["Heading2"], fontName="Helvetica-Bold",
    fontSize=12.5, leading=15, textColor=BLUE, spaceBefore=7, spaceAfter=5,
))
styles.add(ParagraphStyle(
    name="BodyX", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=9.1, leading=12.4, textColor=INK, spaceAfter=5,
))
styles.add(ParagraphStyle(
    name="SmallX", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=7.8, leading=10.2, textColor=INK,
))
styles.add(ParagraphStyle(
    name="HeaderCell", parent=styles["BodyText"], fontName="Helvetica-Bold",
    fontSize=7.3, leading=9.3, textColor=WHITE,
))
styles.add(ParagraphStyle(
    name="TinyX", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=6.9, leading=8.7, textColor=INK,
))
styles.add(ParagraphStyle(
    name="Metric", parent=styles["BodyText"], fontName="Helvetica-Bold",
    fontSize=16, leading=19, textColor=NAVY, alignment=TA_CENTER,
))
styles.add(ParagraphStyle(
    name="MetricLabel", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=7.2, leading=9, textColor=MUTED, alignment=TA_CENTER,
))
styles.add(ParagraphStyle(
    name="BulletX", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=8.6, leading=11.4, textColor=INK, leftIndent=10,
    firstLineIndent=-7, bulletIndent=0, spaceAfter=3,
))
styles.add(ParagraphStyle(
    name="CodeX", fontName="Courier", fontSize=7.2, leading=9.2,
    textColor=colors.HexColor("#E7EEF9"),
))
styles.add(ParagraphStyle(
    name="Callout", parent=styles["BodyText"], fontName="Helvetica",
    fontSize=8.6, leading=11.5, textColor=INK, backColor=AMBER,
    borderColor=colors.HexColor("#E8C76A"), borderWidth=0.6,
    borderPadding=7, spaceBefore=4, spaceAfter=6,
))


def P(text, style="BodyX"):
    return Paragraph(text, styles[style])


def bullet(text):
    return Paragraph(text, styles["BulletX"], bulletText="-")


def code_block(text):
    block = Table([[Preformatted(text, styles["CodeX"])],],
                  colWidths=[CONTENT_W], hAlign="LEFT")
    block.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), NAVY),
        ("BOX", (0, 0), (-1, -1), 0.6, BLUE),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 7),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    return block


def table(data, widths, header=True, font_size=7.5, row_bgs=None):
    converted = []
    for row_index, row in enumerate(data):
        converted.append([
            cell if hasattr(cell, "wrap") else P(
                str(cell), "HeaderCell" if header and row_index == 0 else "SmallX"
            )
            for cell in row
        ])
    t = Table(converted, colWidths=widths, repeatRows=1 if header else 0,
              hAlign="LEFT")
    commands = [
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.35, LINE),
        ("LEFTPADDING", (0, 0), (-1, -1), 5),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5),
        ("TOPPADDING", (0, 0), (-1, -1), 4),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
    ]
    if header:
        commands.extend([
            ("BACKGROUND", (0, 0), (-1, 0), NAVY),
            ("TEXTCOLOR", (0, 0), (-1, 0), WHITE),
            ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
        ])
    for row_index in range(1 if header else 0, len(data)):
        if row_index % 2 == 0:
            commands.append(("BACKGROUND", (0, row_index), (-1, row_index), PALE))
    if row_bgs:
        for row_index, color in row_bgs.items():
            commands.append(("BACKGROUND", (0, row_index), (-1, row_index), color))
    t.setStyle(TableStyle(commands))
    return t


def metrics(items):
    cells = []
    for value, label in items:
        cells.append([P(value, "Metric"), P(label, "MetricLabel")])
    blocks = []
    for value_p, label_p in cells:
        blocks.append(Table([[value_p], [label_p]], colWidths=[CONTENT_W / len(cells) - 5],
                            style=TableStyle([
                                ("BACKGROUND", (0, 0), (-1, -1), PALE),
                                ("BOX", (0, 0), (-1, -1), 0.6, LINE),
                                ("TOPPADDING", (0, 0), (-1, 0), 7),
                                ("BOTTOMPADDING", (0, 1), (-1, 1), 7),
                            ])))
    return Table([blocks], colWidths=[CONTENT_W / len(blocks)] * len(blocks),
                 style=TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP")]))


def page_header_footer(canvas, doc):
    canvas.saveState()
    if doc.page == 1:
        canvas.setFillColor(NAVY)
        canvas.rect(0, PAGE_H - 112 * mm, PAGE_W, 112 * mm, fill=1, stroke=0)
        canvas.setFillColor(BLUE)
        canvas.rect(0, PAGE_H - 116 * mm, PAGE_W, 4 * mm, fill=1, stroke=0)
    else:
        canvas.setFillColor(NAVY)
        canvas.rect(0, PAGE_H - 11 * mm, PAGE_W, 11 * mm, fill=1, stroke=0)
        canvas.setFillColor(WHITE)
        canvas.setFont("Helvetica-Bold", 7.5)
        canvas.drawString(LEFT, PAGE_H - 7.2 * mm, "AGENTIC DEVELOPMENT REFERENCE LAB")
        canvas.setFont("Helvetica", 7)
        canvas.drawRightString(PAGE_W - RIGHT, PAGE_H - 7.2 * mm, "Deliverable 1")
    canvas.setStrokeColor(LINE)
    canvas.line(LEFT, 11 * mm, PAGE_W - RIGHT, 11 * mm)
    canvas.setFillColor(MUTED)
    canvas.setFont("Helvetica", 7)
    canvas.drawString(LEFT, 7.2 * mm, "Yorgo756 | yorgo/reference-lab | 2026-11-10")
    canvas.drawRightString(PAGE_W - RIGHT, 7.2 * mm, f"Page {doc.page}")
    canvas.restoreState()


class SubmissionDoc(SimpleDocTemplate):
    def __init__(self, filename):
        super().__init__(
            filename,
            pagesize=A4,
            leftMargin=LEFT,
            rightMargin=RIGHT,
            topMargin=22 * mm,
            bottomMargin=BOTTOM,
            title="Agentic Development Reference Lab - Deliverable 1",
            author="Yorgo756",
            subject="Eight-stage agentic software engineering reference lab",
        )


story = []

# Cover
story.extend([
    Spacer(1, 14 * mm),
    P("Agentic Development<br/>Reference Lab", "CoverTitle"),
    P("Individual Software Engineering Deliverable 1", "CoverSub"),
    Spacer(1, 53 * mm),
    P("LIBRARY MANAGEMENT REST API", "H2x"),
    P("A Java 17-compatible Spring Boot 3 reference application developed through eight progressive stages with test-first, specification-driven, review, quality, and deployment evidence."),
    Spacer(1, 4 * mm),
    metrics([
        ("8", "progressive stages"),
        ("21/21", "final tests passing"),
        ("82.03%", "instruction coverage"),
        ("71.88%", "branch coverage"),
    ]),
    Spacer(1, 7 * mm),
    table([
        ["Student account", "Branch", "Repository"],
        ["Yorgo756", "yorgo/reference-lab", "github.com/Yorgo756/genie-logiciel-reference-lab"],
    ], [45 * mm, 48 * mm, CONTENT_W - 93 * mm]),
    Spacer(1, 5 * mm),
    P("Evidence note", "H2x"),
    P("This report is based on committed source, test reports, raw command logs, generated quality reports, and live Docker HTTP checks. No screenshots were fabricated. Where a screenshot was not captured, a genuine terminal excerpt is used and labeled."),
    P("Official-handout note", "H2x"),
    P("The official handout was unavailable. Requirements, routes, quality thresholds, and function-point classifications are therefore explicitly provisional and can be reconciled later."),
    PageBreak(),
])

# Page 2
story.extend([
    P("1. Method, scope, and environment", "H1x"),
    P("The work used a repeatable agentic loop: inspect the current state, plan a narrow increment, implement it, run executable verification, review failures and design choices, correct defects, and commit meaningful evidence."),
    table([
        ["Element", "Verified configuration", "Evidence role"],
        ["Application", "Spring Boot 3.5.16; Java release 17", "REST API and domain behavior"],
        ["Data", "Spring Data JPA; H2 in-memory", "Repositories and reproducible seeded state"],
        ["Build", "Maven Wrapper 3.10.0", "Portable compile, test, package, analysis"],
        ["Tests", "JUnit 5, MockMvc, Mockito", "Unit, integration, HTTP, error workflows"],
        ["API docs", "springdoc OpenAPI / Swagger UI", "Machine and browser-readable contract"],
        ["Deployment", "Multi-stage Dockerfile; Temurin 17.0.21", "Verified isolated runtime"],
        ["Quality", "PMD 3.28.0; SpotBugs 4.10.5.0; JaCoCo 0.8.16", "Static findings and measured coverage"],
    ], [29 * mm, 76 * mm, CONTENT_W - 105 * mm]),
    P("Provisional scope decisions", "H2x"),
    bullet("Books, members, borrowings, and reservations are separate persisted resources with generated numeric identifiers."),
    bullet("Loans last 14 calendar days; overdue charges are EUR 0.50 per whole late day and stop at return."),
    bullet("Reservation ownership is the member identifier in the path; authentication and authorization are outside the provisional scope."),
    bullet("H2 state is recreated at startup with five books and three active members."),
    P("Environment issue and response", "H2x"),
    P("Spring Initializr exposed Boot 4 metadata and returned HTTP 400 for the requested 3.5.16 archive. Maven Central was checked, and the Boot 3.5.16 scaffold was created explicitly with the official Maven Wrapper. The host runs Oracle JDK 22.0.2 while Maven compiles for release 17."),
    P("Agent interaction evidence", "H2x"),
    code_block(
        "Student: Continue through Labs 2-8; use real tests, reviews, commits,\n"
        "         Docker verification, estimates, and a Moodle PDF.\n"
        "Agent:   Preserved RED/GREEN evidence, corrected observed failures,\n"
        "         and reported provisional assumptions and remaining limits.",
    ),
    PageBreak(),
])

# Page 3
story.extend([
    P("2. Eight-stage workflow: Labs 1-4", "H1x"),
    table([
        ["Lab", "Implemented and reviewed", "Verification and commits"],
        ["1 - Scaffold", "Book, Member, Borrowing entities; repositories, services, controllers; CRUD; H2; five books and three members.", "Build, automated tests, and HTTP checks. 7748938, d500522"],
        ["2 - Agile/TDD", "Sprint backlog and four stories: borrow 14 days, return/availability, active loans, EUR 0.50 late fee. Duplicate and invalid states rejected.", "RED: 4/4 failed with 404. GREEN: 12 passed. 4f0532e, 801f5bd"],
        ["3 - Specification", "Acceptance criteria committed before production code. Reservations require unavailable books and active members; duplicates blocked; owner list/cancel.", "RED: 3/3 failed with 404. GREEN: 15 passed. 442514a, a0abf14, 8192839"],
        ["4 - Design/deploy", "DTO boundary, mappers, service separation, domain exceptions, global ApiError, OpenAPI/Swagger, multi-stage Dockerfile.", "Container and HTTP verified. 77d7781"],
    ], [20 * mm, 82 * mm, CONTENT_W - 102 * mm]),
    P("Lab 2 RED to GREEN excerpt", "H2x"),
    code_block(
        "RED   ./mvnw -Dtest=BorrowingWorkflowIntegrationTest test\n"
        "      Tests run: 4, Failures: 4 - expected 201, received 404\n"
        "GREEN ./mvnw test\n"
        "      Tests run: 12, Failures: 0, Errors: 0, Skipped: 0",
    ),
    P("Lab 3 specification review", "H2x"),
    bullet("Create reservation: HTTP 201 only when the book is unavailable and the member is active."),
    bullet("Missing book/member/reservation: HTTP 404. Available book, inactive member, or duplicate active reservation: HTTP 409."),
    bullet("Owner-specific list: HTTP 200. Owner cancellation: HTTP 204; repeat or non-owner cancellation is hidden as 404."),
    P("Design review decision", "H2x"),
    P("The generic Lab 1 borrowing endpoint delegates to the same 14-day service workflow as the member route, preventing a bypass of availability and membership rules. Services throw domain exceptions; one controller advice maps them to a stable timestamp/status/error/message/path body."),
    PageBreak(),
])

# Page 4
story.extend([
    P("3. Labs 5-8: refinement and evidence", "H1x"),
    Spacer(1, 4 * mm),
    table([
        ["Lab", "Implemented and reviewed", "Verification and commits"],
        ["5 - Refactoring", "1) Extract LoanPolicy. 2) Encapsulate book/loan transitions. 3) Make active-loan endpoint single-purpose.", "17 tests passed after each small change. baef2ee, ddb4f9e, 3d1b47e"],
        ["6 - Quality", "Configured PMD, SpotBugs, JaCoCo and GQM. Corrected three MissingSerialVersionUID findings.", "PMD 3 to 0; coverage 81.09%/68.75% at stage; 18 SpotBugs warnings disclosed. 9c384f3"],
        ["7 - Test quality", "Added error-shape, validation, missing-resource, and completed-fee workflows. Mutated loan term 14 to 13, then restored it.", "Mutation test failed exactly as expected; restored suite 21/21. 1bbb2a1, 365dd7b, d0a288b"],
        ["8 - Estimation", "129 provisional UFP with visible classifications; three productivity scenarios; evidence-based retrospective.", "No actual hours or measured productivity invented. 98b78e1"],
    ], [20 * mm, 82 * mm, CONTENT_W - 102 * mm]),
    P("Three refactorings: before and after", "H2x"),
    table([
        ["Concern", "Before", "After"],
        ["Loan policy", "Magic 14 and 0.50 inside service", "LoanPolicy owns due-date and fee rules"],
        ["State changes", "Service toggled fields directly", "Book and Borrowing name their transitions"],
        ["Active query", "Boolean parameter rejected false", "Single-purpose active-list operation"],
    ], [36 * mm, 64 * mm, CONTENT_W - 100 * mm]),
    P("Lab 7 deliberate-defect proof", "H2x"),
    code_block(
        "Temporary change: LOAN_DAYS = 13 (not committed)\n"
        "Tests run: 1, Failures: 1\n"
        "JSON path \"$.dueDate\" expected:<2026-11-24> but was:<2026-11-23>\n"
        "Restored: LOAN_DAYS = 14\n"
        "Tests run: 21, Failures: 0, Errors: 0, Skipped: 0",
    ),
    P("Integrity decision", "H2x"),
    P("The broken implementation was never committed. The unedited failing log and the full restored-suite log were committed, while Git diff confirmed LoanPolicy matched the correct baseline."),
    PageBreak(),
])

# Page 5
story.extend([
    P("4. API design and live deployment", "H1x"),
    P("The design separates HTTP DTOs and mapping from transactional services and JPA entities. The same domain workflow serves generic and member-scoped borrowing routes, and one exception boundary produces consistent 400, 404, and 409 responses."),
    table([
        ["HTTP boundary", "Service/domain", "Persistence/output"],
        ["Validated request DTOs", "Book, member, loan, reservation rules", "Four Spring Data repositories"],
        ["Controllers + mappers", "LoanPolicy and state transitions", "H2 for development/test"],
        ["Global ApiError advice", "Domain exceptions, transactions", "Response DTOs + OpenAPI"],
    ], [CONTENT_W / 3] * 3),
    P("Docker problem and correction", "H2x"),
    P("The first `eclipse-temurin:17-jdk-alpine` build lacked a usable ARM64 manifest. The Dockerfile was changed to Jammy JDK/JRE stages. The final image built with manifest-list digest `sha256:1ba0a7d...8ce9fdac` and ran with Temurin 17.0.21."),
    P("Final live-container HTTP checks", "H2x"),
    table([
        ["Request/check", "Observed result"],
        ["GET /api/books", "200; five seeded books"],
        ["GET /v3/api-docs", "200; Library Management API; 11 paths"],
        ["GET /swagger-ui/index.html", "200"],
        ["POST invalid book", "400; standard ApiError"],
        ["POST member 1 borrowing", "201; due 2026-11-24; unavailable"],
        ["POST member 2 reservation", "201; book 1 reserved"],
        ["DELETE owned reservation", "204"],
        ["PUT return", "200; available true"],
        ["PUT second return", "409; already returned"],
    ], [72 * mm, CONTENT_W - 72 * mm]),
    P("Genuine command excerpt", "H2x"),
    code_block(
        "books_count=5\n"
        "openapi_title_and_paths=[\"Library Management API\",11]\n"
        "borrow_status=201 body={\"dueDate\":\"2026-11-24\",\"available\":false}\n"
        "reservation_status=201 ... cancel_status=204\n"
        "return_status=200 ... second_return_status=409",
    ),
    PageBreak(),
])

# Page 6
story.extend([
    P("5. Quality and final verification", "H1x"),
    metrics([
        ("21/21", "tests passing"),
        ("82.03%", "instructions"),
        ("71.88%", "branches"),
        ("0", "PMD violations"),
    ]),
    Spacer(1, 4 * mm),
    table([
        ["GQM question/metric", "Provisional target", "Final actual", "Assessment"],
        ["Automated test result", "100% pass", "21 pass; 0 fail/error/skip", "Met"],
        ["JaCoCo instructions", ">= 70%", "1,219/1,486 = 82.03%", "Met"],
        ["JaCoCo branches", ">= 60%", "23/32 = 71.88%", "Met"],
        ["PMD", "Report and correct genuine finding", "3 initial; 0 after correction", "Met"],
        ["SpotBugs", "Report actionable findings", "18 priority-2 warnings disclosed", "Reviewed"],
        ["HTTP behavior", "201/204/400/404/409 tested", "All represented in workflows", "Met"],
    ], [55 * mm, 37 * mm, 60 * mm, CONTENT_W - 152 * mm]),
    P("Static-analysis review", "H2x"),
    P("PMD identified `MissingSerialVersionUID` in three custom exceptions. Adding the fields was behavior-preserving and reduced PMD from three to zero. SpotBugs still reports 4 `EI_EXPOSE_REP` and 14 `EI_EXPOSE_REP2` warnings around mutable JPA entity references and Spring-injected collaborators. They remain visible rather than being suppressed with a broad filter."),
    P("Final clean command", "H2x"),
    code_block(
        "./mvnw clean verify pmd:pmd spotbugs:spotbugs\n"
        "Tests run: 21, Failures: 0, Errors: 0, Skipped: 0\n"
        "BUILD SUCCESS | Total time: 14.152 s\n"
        "JaCoCo: instructions 82.03%, branches 71.88%\n"
        "PMD: 0 | SpotBugs: 18 disclosed warnings",
    ),
    P("Known warning", "H2x"),
    P("Host JDK 22 reports Mockito's future dynamic-agent compatibility warning. It did not fail compilation or tests and does not alter the Java 17 container runtime; it is retained as a follow-up item."),
    PageBreak(),
])

# Page 7
story.extend([
    P("6. Estimation and retrospective", "H1x"),
    P("The estimate is a classroom planning exercise, not a measurement. The API boundary excludes Swagger UI, build tools, tests, and seed data. Average IFPUG weights are used because data-element and record-element thresholds were not specified."),
    table([
        ["Type", "Count", "Weight", "Points", "Basis"],
        ["EI", "13", "4", "52", "Create/update/delete and workflow commands"],
        ["EO", "1", "5", "5", "Calculated late-fee response"],
        ["EQ", "8", "4", "32", "List/get and member-specific queries"],
        ["ILF", "4", "10", "40", "Books, members, borrowings, reservations"],
        ["EIF", "0", "7", "0", "No external interface file"],
        ["Total UFP", "", "", "129", "No value-adjustment factor"],
    ], [28 * mm, 18 * mm, 18 * mm, 22 * mm, CONTENT_W - 86 * mm]),
    P("Hypothetical effort scenarios", "H2x"),
    table([
        ["Assumed productivity", "Formula", "Hypothetical effort"],
        ["8 FP/person-month", "129 / 8", "16.13 person-months"],
        ["12 FP/person-month", "129 / 12", "10.75 person-months"],
        ["16 FP/person-month", "129 / 16", "8.06 person-months"],
    ], [55 * mm, 45 * mm, CONTENT_W - 100 * mm]),
    P("No measured-hours claim", "Callout"),
    P("No start/stop time log or independently measured development-hours record exists. This submission therefore reports no actual hours and no measured productivity. Commits, tests, coverage, and command results are engineering evidence, not substitutes for elapsed time."),
    P("Retrospective", "H2x"),
    bullet("Worked: small verified increments, RED/GREEN history, specification before reservations, and regression after each refactoring."),
    bullet("Corrected: Initializr 400, reservation 404s before implementation, ARM64 Alpine image failure, and PMD serial-version findings."),
    bullet("Next: reconcile the official handout; add migrations and a persistent profile; add authentication and concurrency tests; review a narrow framework-aware SpotBugs policy."),
    PageBreak(),
])

# Page 8
story.extend([
    P("7. Evidence map, limitations, and conclusion", "H1x"),
    P("The repository preserves one report per stage plus raw logs for the deliberate defect, restored suite, final clean verification, and final Docker HTTP run."),
    table([
        ["Evidence", "Repository location", "Purpose"],
        ["Stage reports", "reference-lab/reports/lab1-report.md ... lab8-report.md", "Commands, results, decisions, issues, commits"],
        ["Interaction record", "reference-lab/reports/agent-interactions.md", "Concise genuine request/action history"],
        ["RED/GREEN artifacts", "docs/sprint-backlog.md; docs/reservation-specification.md", "Backlog and acceptance criteria written before code"],
        ["Quality plan", "docs/gqm.md", "Questions, metrics, provisional targets"],
        ["Mutation logs", "reports/evidence/lab7-*.txt", "Failure and restored-suite output"],
        ["Final logs", "reports/evidence/final-*.txt", "Clean build and container HTTP output"],
        ["Estimation/reflection", "docs/function-point-estimate.md; docs/retrospective.md", "Assumptions, scenarios, learning"],
    ], [34 * mm, 85 * mm, CONTENT_W - 119 * mm]),
    P("Representative Git history", "H2x"),
    table([
        ["Commit(s)", "Meaningful increment"],
        ["4f0532e -> 801f5bd", "Lab 2 lending RED to GREEN"],
        ["442514a -> a0abf14 -> 8192839", "Lab 3 specification, failing acceptance tests, implementation"],
        ["baef2ee / ddb4f9e / 3d1b47e", "Three independently verified refactorings"],
        ["9c384f3", "Quality tooling, metrics, and PMD correction"],
        ["1bbb2a1 / 365dd7b / d0a288b", "Expanded tests, mutation evidence, recovery report"],
        ["98b78e1 / 38c6417", "Estimation, reflection, and final verification evidence"],
    ], [68 * mm, CONTENT_W - 68 * mm]),
    P("Remaining limitations", "H2x"),
    bullet("Official-handout alignment is unverified; all such assumptions are labeled provisional."),
    bullet("H2 data is ephemeral; no production database, migrations, or security layer is claimed."),
    bullet("18 SpotBugs warnings remain disclosed; no claim of zero static-analysis findings is made."),
    bullet("No screenshots were captured automatically; genuine command excerpts and committed raw logs are used instead."),
    P("Conclusion", "H2x"),
    P('The branch contains a working, tested, documented, container-verified Library API and a traceable eight-stage engineering history. The final state is reproducible with the Maven Wrapper and Dockerfile, while uncertainties and unresolved risks remain explicit.<br/><br/><font color="#2563EB"><b>GitHub branch</b></font><br/><link href="https://github.com/Yorgo756/genie-logiciel-reference-lab/tree/yorgo/reference-lab" color="#2563EB">https://github.com/Yorgo756/genie-logiciel-reference-lab/tree/yorgo/reference-lab</link>'),
])


doc = SubmissionDoc(str(OUTPUT))
doc.build(story, onFirstPage=page_header_footer, onLaterPages=page_header_footer)
print(OUTPUT)
