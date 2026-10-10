# BSG Anagrams

Full-stack anagram analysis app — Spring Boot 3.4 (Java 24) backend, Angular 19 frontend, packaged as a single runnable JAR.

---

## Run locally — two options

### Option A: Maven (no Docker needed)

**Requirements:** Java 24, Maven 3.9+

```bash
mvn clean package
java -jar backend/target/bsg-anagrams-backend-1.0.0-SNAPSHOT.jar
```

First build downloads Node automatically for the Angular build. Subsequent builds are fast.

### Option B: Docker

**Requirements:** Docker

```bash
docker compose up
```

That's it. Image is built and started automatically.

---

## What's running at localhost:8080

| URL | What |
|---|---|
| `/` | Angular UI |
| `/swagger-ui.html` | Interactive API docs |
| `/h2-console` | Database console (JDBC: `jdbc:h2:mem:anagramsdb`) |

On first startup the app seeds ~178k words from `Dictionary.txt`. Subsequent restarts skip seeding.

---

## Frontend dev mode

Run the backend first, then in a separate terminal:

```bash
cd frontend/src/main/anagrams-ui
npm install   # first time only
npm start     # http://localhost:4200 — proxies /api/* to :8080
```

---

## API

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/words` | All words — params: `page`, `size`, `sortBy`, `direction` |
| `POST` | `/api/words` | Add a word — body: `{ "word": "SPARE" }` |
| `DELETE` | `/api/words/{word}` | Delete a word |
| `GET` | `/api/words/{word}/anagrams` | Anagrams of a word |
| `GET` | `/api/anagrams/counts` | Anagram group counts per word length + timing (ms) |

---

## Tests

```bash
mvn test -pl backend
```

---

## Deploy to AWS Elastic Beanstalk (free tier)

> **Outstanding:** requires an AWS account + Docker Hub account (both free).

### One-time setup

1. Create a free [AWS account](https://aws.amazon.com/free) and [Docker Hub account](https://hub.docker.com)
2. Install [AWS CLI](https://aws.amazon.com/cli/) and run `aws configure`
3. Install [EB CLI](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/eb-cli3-install.html)
4. Replace `<YOUR_DOCKERHUB_USERNAME>` in `Dockerrun.aws.json`

### First deploy

```bash
docker build -t <YOUR_DOCKERHUB_USERNAME>/bsg-anagrams:latest .
docker push <YOUR_DOCKERHUB_USERNAME>/bsg-anagrams:latest

eb init bsg-anagrams --platform "Docker" --region us-east-1
eb create bsg-anagrams-env --instance-type t2.micro --single
eb open
```

`--single` skips the load balancer — keeps it on the free tier.

### Subsequent deploys

```bash
docker build -t <YOUR_DOCKERHUB_USERNAME>/bsg-anagrams:latest .
docker push <YOUR_DOCKERHUB_USERNAME>/bsg-anagrams:latest
eb deploy
```

---

## Project structure

```
├── backend/        Spring Boot API, JPA, H2, Caffeine cache, Swagger
├── frontend/       Angular 19 SPA (built by Maven, served from the JAR)
├── Dockerfile      Multi-stage build — Maven build stage + slim JRE runtime
├── docker-compose.yml
└── Dockerrun.aws.json   Elastic Beanstalk single-container config
```

---

## How the algorithm works

Each word is stored with a `sorted_chars` column — its letters sorted alphabetically (e.g. `SPARE` → `AEPRS`). All anagrams of the same word share the same value.

Counting anagram groups is a single DB aggregation, never touching Java-side iteration:

```sql
SELECT word_length, COUNT(DISTINCT sorted_chars)
FROM words
GROUP BY word_length
ORDER BY word_length
```

A composite index on `(word_length, sorted_chars)` makes it a covering index scan. With Caffeine caching on top, repeated calls cost nothing — the DB is only hit after a word is added or deleted.

---

## Assumptions

- Anagram count = number of **groups** (e.g. SPARE / REAPS / PARES = 1 group), not total words.
- All words normalised to uppercase internally.
- H2 in-memory DB — swap to PostgreSQL by updating `application.properties` datasource and adding the PG driver.
