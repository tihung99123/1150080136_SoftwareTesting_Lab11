# selenium-framework

This repository contains the Lab 9 Selenium framework and a GitHub Actions CI pipeline for the assignment in Bài 1.

## Project layout

- `1150080136_LeQuocHung_ST_Buoi9/` contains the Lab 9 Maven multi-module project.
- `1150080136_LeQuocHung_ST_Buoi9/bai7/` is the refactored module used for CI.
- `bai7` includes page objects, tests, config readers, and screenshot capture on failure.

## Run locally

```bash
mvn test -f 1150080136_LeQuocHung_ST_Buoi9/bai7/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

## GitHub Actions

The workflow at `.github/workflows/selenium-ci.yml` runs on `push`, `pull_request`, and `workflow_dispatch`.
It uses Java 17 Temurin, Maven cache, and uploads:

- `1150080136_LeQuocHung_ST_Buoi9/bai7/target/surefire-reports/`
- `1150080136_LeQuocHung_ST_Buoi9/bai7/target/screenshots/`

when the test job fails.
