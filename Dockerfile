# ── Stage 1 : Build avec Maven ────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copier pom.xml en premier pour profiter du cache des dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copier le reste du code source
COPY src ./src

# Builder le JAR (sans les tests)
RUN mvn package -Dmaven.test.skip=true -q

# ── Stage 2 : Image finale légère ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier uniquement le JAR depuis le stage builder
COPY --from=builder /app/target/patient-test-*.jar app.jar

# Port exposé
EXPOSE 8086

# Lancement
ENTRYPOINT ["java", "-jar", "app.jar"]
