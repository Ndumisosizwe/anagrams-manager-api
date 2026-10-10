# ── Stage 1: build ──────────────────────────────────────────────────────────
# Uses the official Maven + JDK 24 image — no separate Maven install needed
FROM maven:3.9-eclipse-temurin-24-alpine AS build

WORKDIR /workspace

# Copy POMs first so the dependency download layer is cached
# (only invalidated when a POM file changes, not on every source change)
COPY pom.xml .
COPY backend/pom.xml backend/
COPY frontend/pom.xml frontend/
RUN mvn dependency:go-offline -pl backend -am --no-transfer-progress -q 2>/dev/null || true

# Copy both modules — frontend JAR is needed by the backend unpack step
COPY backend/src backend/src
COPY frontend/src frontend/src

# Full build: Angular (ng build) + backend packaging into a single fat JAR
# Tests are skipped here — run them in your CI pipeline or locally with mvn test
RUN mvn clean package --no-transfer-progress -DskipTests

# ── Stage 2: runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jre-alpine AS runtime

# Run as non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=build /workspace/backend/target/bsg-anagrams-backend-*.jar app.jar

# JVM flags tuned for t2.micro (1 GB RAM total):
#   -Xms128m           start small, let it grow on demand
#   -Xmx512m           cap heap so OS/H2 still have breathing room
#   -XX:+UseSerialGC   lowest GC overhead for single-core/low-memory containers
ENV JAVA_OPTS="-Xms128m -Xmx512m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
