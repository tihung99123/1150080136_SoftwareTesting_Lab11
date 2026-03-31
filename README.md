# Lab 9 Selenium Framework

This repository contains the full Lab 9 Maven multi-module Selenium project.

## Project layout

- `1150080136_LeQuocHung_ST_Buoi9/` is the Lab 9 parent project.
- `bai1` to `bai7` contain the separate exercise modules from the lab.
- `bai2/` is the standalone folder used by the GitHub Actions matrix pipeline.

## Run locally

To run the CI smoke suite locally:

```bash
mvn test -f bai2/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

You can also run a specific module directly, for example:

```bash
mvn test -f bai2/pom.xml
```

## GitHub Actions

The workflow at `.github/workflows/selenium-ci.yml` runs on `push` to `main`, `pull_request`, and `workflow_dispatch`.
It uses Java 17 Temurin, Maven cache, and runs `bai2` in a matrix for `chrome` and `firefox`.
It uploads these artifacts when tests fail:

- `bai2/target/surefire-reports/`
- `bai2/target/screenshots/`
