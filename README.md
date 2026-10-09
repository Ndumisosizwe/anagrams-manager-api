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
└── frontend/                        ← Angular 19 SPA
    ├── pom.xml                      ← frontend-maven-plugin wires ng build into mvn package
    └── src/main/anagrams-ui/
        └── src/app/
            ├── api.service.ts       ← all HTTP calls
            ├── shared/              reusable AlertComponent
            └── tabs/                words-list, add-word, anagram-lookup, anagram-counts
```

---

## Build

```bash
# Backend only
mvn clean package -pl backend

# Full build — backend + Angular (downloads Node locally on first run)
mvn clean package
```

The full build produces:
- `backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar` — runnable fat JAR containing the API

---

## Run

```bash
java -jar backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar
```

On startup the application seeds the database from `Dictionary.txt` (one-time, skipped on subsequent restarts).

---

## Frontend — dev mode

Start the backend first, then in a separate terminal:

```bash
cd frontend/src/main/anagrams-ui
npm install        # first time only
npm start          # http://localhost:4200 — proxies /api to localhost:8080
```

---

## URLs

| Resource | URL |
|---|---|
| Angular UI (dev) | http://localhost:4200 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

H2 console JDBC URL: `jdbc:h2:mem:anagramsdb`

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/words` | All words — params: `page`, `size`, `sortBy`, `direction` |
| `POST` | `/api/words` | Add a word — body: `{ "word": "LISTEN" }` |
| `DELETE` | `/api/words/{word}` | Delete a word |
| `GET` | `/api/words/{word}/anagrams` | Anagrams of a given word |
| `GET` | `/api/anagrams/counts` | Anagram group counts per word length + computation time (ms) |

Full interactive docs available at `/swagger-ui.html`.

---

## Tests

```bash
mvn test
```

15 tests — unit (service + algorithm) and slice tests (MockMvc controllers).

---

## Assumptions

- **Anagram count** = number of *groups* where 2+ words share the same sorted-character signature (e.g. LISTEN / SILENT / ENLIST = 1 group). Not the total number of anagram words.
- Words are stored and compared **case-insensitively** — all normalised to uppercase internally.
- **H2 in-memory** database is used for portability. To switch to PostgreSQL, update the `datasource` block in `application.properties` and add the PostgreSQL driver dependency.
- Dictionary is seeded **once on first startup**. Subsequent restarts skip seeding if the table is already populated.
- The anagram counts result is **cached** (Caffeine) and automatically evicted whenever a word is added or deleted, keeping results consistent with live data.
