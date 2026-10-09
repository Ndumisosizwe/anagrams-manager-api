# BSG Anagrams — Code Assignment

A full-stack anagram analysis application built with **Java 24 + Spring Boot** (backend) and **Angular 19** (frontend), structured as a multi-module Maven project.

---

## Requirements

- Java 24 (Amazon Corretto 24 or equivalent)
- Maven 3.9+
- Node.js 20+ and npm *(only needed for Angular dev mode — the Maven build downloads Node automatically)*

---

## Project Structure

```
BSG-anagrams-code assignment/
├── pom.xml                          ← parent POM
├── backend/                         ← Spring Boot REST API
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/bsg/anagrams/
│       │   ├── config/              OpenApiConfig, WebConfig (CORS)
│       │   ├── controller/          WordController, AnagramController
│       │   ├── domain/              Word (JPA entity)
│       │   ├── dto/                 Request/Response records, PagedResponse
│       │   ├── exception/           GlobalExceptionHandler
│       │   ├── repository/          WordRepository
│       │   └── service/             WordService, AnagramService, DataLoaderService
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── Dictionary.txt       ← seeded into H2 on first startup
│       └── test/java/com/bsg/anagrams/
│           ├── controller/          WordControllerTest, AnagramControllerTest
│           └── service/             AnagramServiceTest
└── frontend/                        ← Angular 19 SPA (Maven module)
    ├── pom.xml                      ← frontend-maven-plugin runs ng build; assets unpacked into backend JAR
    └── src/main/anagrams-ui/
        └── src/app/
            ├── api.service.ts       ← all HTTP calls
            ├── shared/              reusable AlertComponent
            └── tabs/                words-list, add-word, anagram-lookup, anagram-counts
```

---

## Build

```bash
# Backend only (skips Angular build — useful during backend development)
mvn clean package -pl backend

# Full build — Angular + backend, single runnable JAR
mvn clean package
```

The full build:
1. Downloads Node locally via `frontend-maven-plugin` (first run only)
2. Runs `ng build --configuration production`
3. Unpacks the Angular `dist/` assets into the backend classpath under `META-INF/resources/`
4. Produces a single fat JAR: `backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar`

---

## Run

```bash
java -jar backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar
```

On startup the app seeds the database from `Dictionary.txt` (one-time only — skipped on subsequent restarts).

---

## What you get at localhost:8080

| URL | Description |
|---|---|
| `http://localhost:8080/` | Angular UI |
| `http://localhost:8080/swagger-ui.html` | Swagger / OpenAPI docs |
| `http://localhost:8080/api-docs` | OpenAPI JSON |
| `http://localhost:8080/h2-console` | H2 database console |

H2 console JDBC URL: `jdbc:h2:mem:anagramsdb`

---

## Frontend — dev mode

For live-reload Angular development, run the backend first then:

```bash
cd frontend/src/main/anagrams-ui
npm install        # first time only
npm start          # http://localhost:4200 — proxies /api/* to localhost:8080
```

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/words` | All words — params: `page`, `size`, `sortBy`, `direction` |
| `POST` | `/api/words` | Add a word — body: `{ "word": "LISTEN" }` |
| `DELETE` | `/api/words/{word}` | Delete a word |
| `GET` | `/api/words/{word}/anagrams` | Anagrams of a given word |
| `GET` | `/api/anagrams/counts` | Anagram group counts per word length + computation time (ms) |

Full interactive docs at `http://localhost:8080/swagger-ui.html`.

---

## Tests

```bash
mvn test -pl backend
```

15 tests — unit (anagram algorithm, domain logic) and MockMvc slice tests (all controllers).

---

## Assumptions

- **Anagram count** = number of *groups* where 2+ words share the same sorted-character signature (e.g. LISTEN / SILENT / ENLIST = 1 group of 3), not the total number of anagram words.
- Words are stored and compared **case-insensitively** — normalised to uppercase internally.
- **H2 in-memory** database is used for portability. To swap to PostgreSQL, update the `datasource` block in `application.properties` and add the PostgreSQL driver dependency.
- Dictionary is seeded **once on first startup**. Subsequent restarts skip seeding if the table is already populated.
- Anagram counts are **cached** (Caffeine) and automatically evicted on any word add or delete, so results always reflect live data.
