# Food Delivery Fee Calculator

Spring Boot bootstrap for the Fujitsu Java trial task
## Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- H2
- Maven Wrapper

## Run locally

1. Start the application:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

2. Open the bootstrap endpoint:

```text
GET http://localhost:8080/api/v1/system/status
```

3. Optional H2 console in local profile:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:food_delivery_fee_calculator
```

## Run tests

```powershell
.\mvnw.cmd test
```

## Current structure

- `ee.fujitsu.fooddeliveryfeecalculator.api` for HTTP controllers and request/response exposure
- `ee.fujitsu.fooddeliveryfeecalculator.application` for orchestration logic
- `ee.fujitsu.fooddeliveryfeecalculator.configuration` for Spring configuration and typed properties
- `ee.fujitsu.fooddeliveryfeecalculator.domain` for domain models
- `ee.fujitsu.fooddeliveryfeecalculator.infrastructure` for database and external system integration

## Persistence

- Weather observations are stored in the `weather_observation` table.
- The schema lives in `src/main/resources/schema.sql`.
- Hibernate validates the schema at startup instead of generating it silently.
- Each import must insert a new row with its own generated primary key so station history is preserved.
- Persistence code can query the latest observation for a station, an exact station/timestamp match, and full station history in chronological order.

## External weather feed

- The official observation XML feed is configured through `weather.api.observations-url`.
- The integration layer parses only the supported stations required by the task: Tallinn-Harku, Tartu-Toravere, and Parnu.
- Parsing errors are explicit: malformed XML, duplicate supported stations, missing required fields, or missing required stations all fail fast.

## Scheduled import

- The weather import job runs on the cron expression configured in `weather.import.cron`.
- The default schedule is `0 15 * * * *`, which means every hour at `HH:15:00`.
- The scheduled component only triggers the import and logs failures; the actual import logic runs inside a transactional application service.
- If persistence fails during an import, the whole import transaction is rolled back so partial history is not left in the database.

## Current bootstrap endpoint

The bootstrap endpoint verifies that the application starts and that the configured H2 database is reachable.
