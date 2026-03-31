# Lab 9 Selenium Framework

This repository contains the full Lab 9 Maven multi-module Selenium project.

## Project layout

- `1150080136_LeQuocHung_ST_Buoi9/` is the Lab 9 parent project.
- `bai1` to `bai7` contain the separate exercise modules from the lab.
- `bai7` is the consolidated module used by the GitHub Actions smoke pipeline.

## Run locally

To run the CI smoke suite locally:

```bash
mvn test -f 1150080136_LeQuocHung_ST_Buoi9/bai7/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

You can also run a specific module directly, for example:

```bash
mvn test -f 1150080136_LeQuocHung_ST_Buoi9/bai2/pom.xml
```

## GitHub Actions

The workflow at `.github/workflows/selenium-ci.yml` runs on `push` to `main`, `pull_request`, and `workflow_dispatch`.
It uses Java 17 Temurin, Maven cache, and runs the full Lab 9 multi-module suite from the parent project.
It uploads these artifacts when tests fail:

- `1150080136_LeQuocHung_ST_Buoi9/**/target/surefire-reports/`
- `1150080136_LeQuocHung_ST_Buoi9/**/target/screenshots/`
