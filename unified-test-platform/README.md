# Unified Test Automation Platform

**One platform to validate UI, API, DB, and contracts seamlessly**

## Overview

This project is an enterprise-style, modular test automation stack built on **Java 17**, **TestNG**, and **Maven**. It provides a single shared **test context** and consistent reporting so teams can validate **UI (Playwright)**, **HTTP APIs (REST Assured)**, **relational data (JDBC + HikariCP)**, and **consumer-driven contracts (Pact)** in one pipeline, including **Allure** reporting and **GitHub Actions** CI.

## Architecture (text diagram)

```
┌─────────────────────────────────────────────────────────────────┐
│                     TestNG + Allure + Log4j2                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
 ┌─────────────┐     ┌─────────────┐     ┌──────────────┐
 │  UI (POM)   │     │ REST Assured │     │ JDBC + Pool  │
 │ Playwright  │     │ + clients    │     │ QueryExecutor│
 └──────┬──────┘     └──────┬───────┘     └──────┬───────┘
        │                   │                     │
        └───────────┬───────┴──────────┬──────────┘
                    ▼                  ▼
            ┌──────────────┐   ┌─────────────────┐
            │ TestContext  │   │ Pact + MockSrv  │
            │ (ThreadLocal)│   │ (contracts)     │
            └──────────────┘   └─────────────────┘
                    │
                    ▼
            ┌───────────────────────────┐
            │ Docker: Postgres +        │
            │ WireMock + demo UI (nginx)│
            └───────────────────────────┘
```

## Features

| Area | Capabilities |
|------|----------------|
| **UI** | Page Object Model, reusable `UIActions`, Playwright auto-waits, failure screenshots to Allure |
| **API** | REST Assured client abstraction, request/response logging (Log4j2), JSON Schema validation |
| **DB** | HikariCP pool, `QueryExecutor` for parameterized SQL, assertions after API/UI steps |
| **Contract** | Pact consumer tests with embedded mock provider, pact JSON under `target/pacts` |

## Tech stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| Tests | TestNG |
| Build | Maven |
| UI | Playwright (Java) |
| API | REST Assured + json-schema-validator |
| Database | JDBC, HikariCP, PostgreSQL / MySQL drivers |
| Contracts | Pact JVM (`au.com.dius.pact:consumer`) |
| Reporting | Allure |
| Logging | Log4j2 |
| Config | YAML (`config.yaml`) + `ConfigManager` |
| CI | GitHub Actions |
| Local services | Docker Compose (Postgres, WireMock, nginx demo UI) |

## How it works (flow)

1. **Config** resolves environment from `ENV` or `-Denv` (default `dev`) and loads `src/test/resources/config.yaml`.
2. **TestContext** (thread-local) carries identifiers (for example `userId`, `email`) across UI, API, and DB steps.
3. **End-to-end** tests drive the browser against the demo UI; the UI calls the **WireMock**-backed API; tests then assert via **REST Assured**, persist or verify rows with **JDBC**, and run **Pact** consumer verification against an embedded **MockServer**.
4. **Allure** collects steps, API attachments (via `AllureRestAssured`), DB text attachments, screenshots on failure, and contract notes.

## Setup

1. **JDK 17** and **Maven 3.9+**
2. Clone the repository and open `unified-test-platform/`.
3. Install Playwright browsers for the Java binding (once per machine):

```bash
cd unified-test-platform
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

4. Start local dependencies:

```bash
docker compose -f docker/docker-compose.yml up -d
```

5. Optional: set environment (defaults to `dev`):

```bash
export ENV=dev
```

## Running tests

From `unified-test-platform/`:

```bash
mvn clean test
```

- **Suite file**: `src/test/resources/testng.xml` (groups: `smoke`, `regression`, `contract`).
- **Parallelism**: suite is configured with `parallel="false"` by default for stable JDBC usage; adjust in `testng.xml` if needed.
- **Retry**: opt in per test with `@Test(retryAnalyzer = com.omnitest.platform.utils.RetryAnalyzer.class)`.
- **Allure report** (after a test run):

```bash
mvn allure:report
# Open target/site/index.html in a browser (exact folder name may vary by allure-maven version)
```

## CI/CD integration

The workflow [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) runs on push and pull request to `master` / `main`:

- Sets up JDK 17 with Maven cache
- Installs Playwright browsers (`com.microsoft.playwright.CLI`)
- Starts Docker Compose services
- Runs `mvn clean test` in `unified-test-platform`
- Generates an Allure report and uploads **Allure** + **`target/pacts`** as artifacts

## Sample report output

After `mvn test` and `mvn allure:report`, Allure typically includes:

- **Suites / Behaviors**: grouped by `@Feature` / `@Story`
- **Attachments**: REST request/response (Allure REST Assured), DB row dumps, Pact summary text, UI screenshots on failure
- **Timeline**: TestNG method ordering and duration

## Future enhancements

- Provider-side Pact verification job and pact broker publishing
- Testcontainers for on-demand Postgres/WireMock in CI
- Separate `smoke` vs `full` Maven profiles and TestNG `<suite>` files
- Centralized secret management for non-local environments
