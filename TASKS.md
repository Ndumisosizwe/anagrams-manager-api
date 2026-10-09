# BSG Anagrams Code Assignment — Sub-Tasks

## Tech Stack
- **Backend:** Java 24 + Spring Boot 3.x (Maven)
- **Frontend:** React (Vite + TypeScript)
- **Persistence:** H2 (in-memory, dev) → easily swappable to PostgreSQL
- **Build tool:** Maven (multi-module project)

---

## Project Structure (target)
```
BSG-anagrams-code assignment/
├── backend/                  # Spring Boot Maven project
│   ├── src/
│   │   ├── main/java/...
│   │   └── test/java/...
│   ├── src/main/resources/
│   │   └── Dictionary.txt    # copied from root
│   └── pom.xml
├── frontend/                 # React + Vite app
│   ├── src/
│   └── package.json
└── TASKS.md
```

---

## Sub-Tasks

### TASK 1 — Maven Project Scaffold (Backend)
Set up the Spring Boot 3.x / Java 24 Maven project skeleton.
- Generate `pom.xml` with dependencies: Spring Web, Spring Data JPA, H2, Lombok, Validation, Spring Cache
- Create the main application class
- Copy `Dictionary.txt` into `src/main/resources/`
- Configure `application.properties` (H2, JPA, caching)

**Deliverable:** A compilable, runnable Spring Boot app (empty endpoints, no logic yet)

---

### TASK 2 — Domain Model & Database Layer
Define the data model and persistence layer.
- `Word` JPA entity (id, word, length, createdAt)
- `WordRepository` extending `JpaRepository` with custom query methods
- `DataLoader` component: reads `Dictionary.txt` on startup and seeds the database (only if empty)
- Confirm H2 console accessible at `/h2-console`

**Deliverable:** App starts, database seeded with all dictionary words, queryable via H2 console

---

### TASK 3 — Anagram Algorithm
Implement the core anagram-counting algorithm efficiently.
- Define what makes two words anagrams: same sorted character signature (e.g. "LISTEN" → "EILNST")
- `AnagramService`: groups all words by their sorted-char key, counts anagram groups per word length
- Algorithm must run in O(n · k log k) — n = word count, k = max word length
- Result format: `{ wordLength: x, anagramCount: y }` list + total execution time in ms
- The service must be dynamic: re-computes from the live database (respects adds/deletes), with result caching invalidated on mutation

**Deliverable:** Unit-tested `AnagramService` with correct counts and timing

---

### TASK 4 — REST API (Spring MVC Controllers)
Expose all required endpoints.

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/words` | Retrieve all words (with pagination + sorting) |
| `POST` | `/api/words` | Add a new word |
| `DELETE` | `/api/words/{word}` | Delete a word by value |
| `GET` | `/api/words/{word}/anagrams` | Get anagrams for a given word |
| `GET` | `/api/anagrams/counts` | Counts of anagrams per word length + timing |

- Request/response DTOs (no entity exposure)
- Global exception handler (`@RestControllerAdvice`)
- Input validation (`@Valid`, `@NotBlank`)
- CORS configured for React frontend (localhost:5173)

**Deliverable:** All endpoints tested with curl / Postman and returning correct JSON

---

### TASK 5 — Caching Layer
Add Spring Cache (`@Cacheable`, `@CacheEvict`) to the anagram algorithm.
- Cache the anagram counts result
- Evict cache on any word add or delete
- Use Caffeine or simple ConcurrentMapCache

**Deliverable:** Second call to `/api/anagrams/counts` is noticeably faster (no recomputation)

---

### TASK 6 — React Frontend Scaffold
Set up the React + Vite + TypeScript frontend project.
- `npm create vite@latest frontend -- --template react-ts`
- Install Axios for HTTP, a UI component library (e.g. shadcn/ui or plain CSS/Tailwind)
- Configure API base URL pointing to `http://localhost:8080`
- Routing setup (React Router)

**Deliverable:** React dev server starts, blank shell with navigation

---

### TASK 7 — Frontend Features
Implement all UI screens/components.

1. **All Words page** — fetch & display paginated list of all words; delete button per word
2. **Add Word form** — input + submit; shows success/error feedback
3. **Anagram Lookup** — enter a word, display its anagrams from the API
4. **Anagram Counts page** — display the table `Words with the character length of X had Y anagrams` + total execution time in ms

**Deliverable:** Fully working SPA satisfying all three frontend requirements from the spec

---

### TASK 8 — Automated Tests
Write tests for the backend.
- Unit tests: `AnagramServiceTest` (algorithm correctness, edge cases)
- Integration tests: `WordControllerTest` using `@SpringBootTest` + MockMvc
- Cover: add word, delete word, get anagrams, get counts

**Deliverable:** `mvn test` passes green

---

### TASK 9 — Polish & Production Readiness
Final cleanup before submission.
- Add Javadoc / inline comments documenting assumptions
- README with: how to build, how to run backend, how to run frontend, sample API calls
- Verify correct output matches spec: `Words with the character length of x had y anagrams`
- Remove dev artefacts (`.class` files, `target/`, `node_modules/`) from zip scope via `.gitignore`

**Deliverable:** Clean, submission-ready project zip

---

## Task Order (recommended)
```
TASK 1 → TASK 2 → TASK 3 → TASK 4 → TASK 5 → TASK 6 → TASK 7 → TASK 8 → TASK 9
```
Each task builds on the previous. Start with TASK 1 when ready.
