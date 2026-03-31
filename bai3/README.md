# Bai3 Standalone

This folder contains the GitHub Secrets version of Lab 9 Bài 3.

## Local setup

Copy the keys from [.env.example](.env.example) into your environment before running tests.

## Run

```bash
mvn test -f bai3/pom.xml -Dbrowser=chrome -Denv=dev -DsuiteXmlFile=testng-smoke.xml
```

The smoke suite reads credentials from environment variables first, then falls back to `config.properties` placeholders.