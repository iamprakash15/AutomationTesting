# AutomationTesting

A small Java 17 project for Selenium browser automation and Excel-based test data.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- Google Chrome for the browser example

Selenium Manager selects and downloads the compatible browser driver automatically. Do not commit driver executables to the repository.

## Build and test

```bash
cd TestDemoMavenProject
mvn clean verify
```

## Run the Amazon search example

Interactive browser:

```bash
mvn exec:java -Dexec.args="cricket bat"
```

Headless browser:

```bash
mvn -Dheadless=true exec:java -Dexec.args="cricket bat"
```

You can override the target site through `-DbaseUrl=https://...` or the `AMAZON_BASE_URL` environment variable. The example deliberately has no credentials and should only be run in accordance with the target site's terms and automated-access rules.

## Read Excel test data

Call `ExcelReader.read(path, sheetName)` to obtain immutable rows of formatted cell values. The reader supports strings, numbers, booleans, formulas, and blank cells, and it closes all file and workbook resources automatically.

## Project layout

```text
TestDemoMavenProject/
├── pom.xml
└── src/
    ├── main/java/io/github/iamprakash15/automationtesting/
    └── test/java/io/github/iamprakash15/automationtesting/
```
