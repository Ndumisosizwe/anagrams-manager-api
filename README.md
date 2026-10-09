# BSG Anagrams — Code Assignment

## Requirements
- Java 24 (Amazon Corretto or any JDK 24)
- Maven 3.9+

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar
```

## URLs

| Resource | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

H2 console JDBC URL: `jdbc:h2:mem:anagramsdb`

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/words?page=0&size=50&sortBy=word&direction=asc` | All words (paginated) |
| POST | `/api/words` | Add a word — body: `{ "word": "LISTEN" }` |
| DELETE | `/api/words/{word}` | Delete a word |
| GET | `/api/words/{word}/anagrams` | Anagrams of a given word |
| GET | `/api/anagrams/counts` | Anagram group counts per word length + timing |

## Run Tests

```bash
mvn test
```

## Project Structure

```
BSG-anagrams-code assignment/
├── pom.xml                  ← parent POM
├── backend/                 ← Spring Boot API (Java 24)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/bsg/anagrams/
│       │   ├── AnagramsApplication.java
│       │   ├── config/          OpenApiConfig, WebConfig
│       │   ├── controller/      WordController, AnagramController
│       │   ├── domain/          Word (JPA entity)
│       │   ├── dto/             Request/Response records
│       │   ├── exception/       GlobalExceptionHandler
│       │   ├── repository/      WordRepository
│       │   └── service/         WordService, AnagramService, DataLoaderService
│       └── test/java/com/bsg/anagrams/
│           ├── controller/      WordControllerTest, AnagramControllerTest
│           └── service/         AnagramServiceTest
└── frontend/                ← Angular (placeholder — implementation pending)
    └── pom.xml
```

## Assumptions
- Anagram count per word length = number of **groups** with 2+ words sharing the same sorted-character signature, not total words.
- Words are stored and compared case-insensitively (all uppercased internally).
- H2 in-memory database is used for portability; swap to PostgreSQL by changing `application.properties` datasource config and adding the PG driver dependency.
- The dictionary is seeded once on first startup; subsequent restarts skip the seed if data already exists.
