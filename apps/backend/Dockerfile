# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Builder
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache Maven dependencies before copying source code.
# This layer is only invalidated when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline --no-transfer-progress

# Copy source and build the fat JAR, skipping tests.
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runner
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runner

# Create a non-root system group and user for security hardening.
RUN addgroup -S bazario && adduser -S -G bazario bazario

WORKDIR /app

# Copy only the compiled artifact from the builder stage.
COPY --from=builder /build/target/*.jar app.jar

# Transfer ownership to the non-root user.
RUN chown bazario:bazario app.jar

USER bazario

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseG1GC", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-jar", "app.jar"]
