# Lab 9 Selenium Framework

[![Full Selenium CI Pipeline](https://github.com/tihung99123/1150080136_SoftwareTesting_Lab11/actions/workflows/selenium-full.yml/badge.svg)](https://github.com/tihung99123/1150080136_SoftwareTesting_Lab11/actions/workflows/selenium-full.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-brightgreen?logo=github)](https://tihung99123.github.io/1150080136_SoftwareTesting_Lab11/)

This repository contains the Lab 9 Selenium exercise folders used in this workspace.

## Project layout

- `bai1` to `bai7` contain the separate exercise modules from the lab.
- `bai2/` is the standalone folder used by the GitHub Actions matrix pipeline.
- `bai3/` is the standalone folder used by the GitHub Secrets pipeline.
- `bai4/` is the standalone folder used for Selenium Grid with Docker.
- `bai5/` is the standalone folder with Allure Report advanced annotations.
- `bai6/` is the full CI pipeline that publishes Allure Report to GitHub Pages.

## Run locally

To run the CI smoke suite locally:

```bash
mvn test -f bai2/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

For Bai 3, copy the values from `bai3/.env.example` into your environment and run:

```bash
mvn test -f bai3/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

For Bai 4, start Selenium Grid first and then run the grid suite:

```bash
docker compose -f bai4/docker-compose.yml up -d
mvn test -f bai4/pom.xml -Dgrid.url=http://localhost:4444 -DsuiteXmlFile=testng-grid.xml
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

The Bai 3 workflow at `.github/workflows/bai3-secrets.yml` reads GitHub Secrets and echoes the password env to verify masking.

## Bai 5 – Allure Report Nâng Cao

```bash
# Chạy tests và tạo Allure report
mvn clean test -f bai5/pom.xml
mvn allure:serve -f bai5/pom.xml
```

## Bai 6 – Full CI Pipeline + Allure GitHub Pages

Workflow `selenium-full.yml`:
- **Trigger**: push to `main` + cron `0 2 * * 1-5` (2AM weekdays)
- **Job `test`**: matrix [chrome, firefox], runs `bai5` smoke tests, uploads `allure-results-{browser}` artifacts
- **Job `publish-report`**: downloads both artifacts → generates Allure Report → deploys to `gh-pages` branch

**Xem Allure Report trực tiếp:**
👉 [https://tihung99123.github.io/1150080136_SoftwareTesting_Lab11/](https://tihung99123.github.io/1150080136_SoftwareTesting_Lab11/)

**Kích hoạt GitHub Pages:** Settings → Pages → Source: Deploy from a branch → Branch: `gh-pages`

**GitHub Secrets cần thêm** (Settings → Secrets → Actions):
| Secret name               | Value             |
|---------------------------|-------------------|
| `SAUCEDEMO_USERNAME`      | `standard_user`   |
| `SAUCEDEMO_PASSWORD`      | `secret_sauce`    |
