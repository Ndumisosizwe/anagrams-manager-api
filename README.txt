BSG ANAGRAMS — CODE ASSIGNMENT
===============================

A full-stack anagram analysis application built with Java 24 + Spring Boot (backend)
and Angular 19 (frontend), structured as a multi-module Maven project.


REQUIREMENTS
------------
- Java 24 (Amazon Corretto 24 or equivalent)
- Maven 3.9+
- Node.js 20+ and npm (only needed for Angular dev mode — the Maven build downloads Node automatically)


PROJECT STRUCTURE
-----------------
BSG-anagrams-code assignment/
├── pom.xml                          Parent POM
├── backend/                         Spring Boot REST API
│   └── src/
│       ├── main/java/com/bsg/anagrams/
│       │   ├── config/              OpenApiConfig, WebConfig (CORS)
│       │   ├── controller/          WordController, AnagramController
│       │   ├── domain/              Word (JPA entity)
│       │   ├── dto/                 Request/Response records, PagedResponse
│       │   ├── exception/           GlobalExceptionHandler
│       │   ├── repository/          WordRepository
│       │   └── service/             WordService, AnagramService, DataLoaderService
│       └── main/resources/
│           ├── application.properties
│           └── Dictionary.txt       Seeded into H2 on first startup
└── frontend/                        Angular 19 SPA (Maven module)
    └── src/main/anagrams-ui/
        └── src/app/
            ├── api.service.ts       All HTTP calls
            ├── shared/              Reusable AlertComponent
            └── tabs/                words-list, add-word, anagram-lookup, anagram-counts


BUILD
-----
Backend only (skips Angular build):
    mvn clean package -pl backend

Full build — Angular + backend, single runnable JAR:
    mvn clean package

The full build:
  1. Downloads Node locally via frontend-maven-plugin (first run only)
  2. Runs ng build --configuration production
  3. Unpacks Angular dist/ assets into the backend classpath under META-INF/resources/
  4. Produces: backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar


RUN
---
    java -jar backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar

On startup the app seeds the database from Dictionary.txt (one-time only).


URLS AT LOCALHOST:8080
----------------------
http://localhost:8080/               Angular UI
http://localhost:8080/swagger-ui.html  Swagger / OpenAPI docs
http://localhost:8080/api-docs       OpenAPI JSON
http://localhost:8080/h2-console     H2 database console

H2 console JDBC URL: jdbc:h2:mem:anagramsdb


FRONTEND — DEV MODE
-------------------
Run the backend first, then:

    cd frontend/src/main/anagrams-ui
    npm install        (first time only)
    npm start          http://localhost:4200 — proxies /api/* to localhost:8080


API ENDPOINTS
-------------
GET    /api/words                       All words (params: page, size, sortBy, direction)
POST   /api/words                       Add a word  (body: { "word": "SPARE" })
DELETE /api/words/{word}                Delete a word
GET    /api/words/{word}/anagrams       Anagrams of a given word
GET    /api/anagrams/counts             Anagram group counts per word length + timing (ms)

Full interactive docs at http://localhost:8080/swagger-ui.html


TESTS
-----
    mvn test -pl backend

15 tests — unit (anagram algorithm, domain logic) and MockMvc slice tests (all controllers).


ASSUMPTIONS
-----------
- Anagram count = number of GROUPS where 2+ words share the same sorted-character
  signature (e.g. SPARE / REAPS / PARES = 1 group of 3), not the total word count.
- Words are stored and compared case-insensitively — normalised to uppercase internally.
- H2 in-memory database is used for portability. To swap to PostgreSQL, update the
  datasource block in application.properties and add the PostgreSQL driver dependency.
- Dictionary is seeded once on first startup. Subsequent restarts skip seeding.
- Anagram counts are cached (Caffeine) and evicted on any word add or delete.


ALGORITHM — THE sorted_chars COLUMN
-------------------------------------
Two words are anagrams if they contain exactly the same letters in any order.
Sorting both words' characters alphabetically produces an identical result for all anagrams:

    SPARE  →  sort letters  →  AEPRS
    REAPS  →  sort letters  →  AEPRS
    PARES  →  sort letters  →  AEPRS

That sorted string is the anagram fingerprint. It is computed ONCE at insert time
and stored as the sorted_chars column:

    public Word(String word) {
        this.word = word.toUpperCase();
        this.sortedChars = sortChars(this.word);
    }

    private static String sortChars(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

The database row for each word looks like:

    word="SPARE"  word_length=5  sorted_chars="AEPRS"
    word="REAPS"  word_length=5  sorted_chars="AEPRS"
    word="PARES"  word_length=5  sorted_chars="AEPRS"
    word="HELLO"  word_length=5  sorted_chars="EHLLO"

Because sorted_chars is pre-stored, counting anagram groups across the whole
dictionary is a single SQL aggregation — no Java-side iteration needed:

    SELECT word_length, COUNT(DISTINCT sorted_chars)
    FROM words
    GROUP BY word_length
    ORDER BY word_length

  - GROUP BY word_length       = one result row per word length
  - COUNT(DISTINCT sorted_chars) = how many unique fingerprints in that group

WHY THIS IS FAST
----------------
  Naive approach:   fetch all 170k+ words → sort each word's chars in Java → group in Java
  This approach:    single DB aggregation returning ~30 rows (one per word length)

The sorting work is done once per word at insert time, not at query time.

A composite index on (word_length, sorted_chars) makes this a COVERING INDEX SCAN —
the database answers the query entirely from the index, never reading the table rows.

With Caffeine caching on top, repeated calls to /api/anagrams/counts return the
pre-computed result from memory. The database is not touched until a word is
added or deleted.
