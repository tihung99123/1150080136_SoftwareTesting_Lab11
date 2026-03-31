# Bai 4 - Selenium Grid With Docker

This standalone module demonstrates Lab 9 Bai 4 with Docker-based Selenium Grid.

## Grid Setup

1. Start the Grid:

```bash
docker compose -f bai4/docker-compose.yml up -d
```

2. Check the containers:

```bash
docker ps
```

3. Open the Grid console:

```text
http://localhost:4444
```

## Run Tests

Run the Grid suite with Chrome and Firefox in parallel:

```bash
mvn test -f bai4/pom.xml -Dgrid.url=http://localhost:4444 -DsuiteXmlFile=testng-grid.xml
```

Run the local suite without Docker:

```bash
mvn test -f bai4/pom.xml -Dbrowser=chrome -DsuiteXmlFile=testng.xml
```

## Notes

- `testng-grid.xml` uses `parallel="tests"` with 4 test sections.
- `BaseTest` uses `RemoteWebDriver` automatically when `-Dgrid.url` is present.
- Failure screenshots are saved under `bai4/target/screenshots/`.