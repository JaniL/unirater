FROM maven:3.9.6-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml /app/pom.xml
COPY src /app/src

RUN mvn -DskipTests package

FROM eclipse-temurin:8-jre

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5 CMD curl -fsS http://127.0.0.1:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
