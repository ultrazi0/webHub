FROM eclipse-temurin:21.0.5_11-jdk-alpine AS builder
ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY gradle $APP_HOME/gradle
COPY gradlew $APP_HOME
COPY settings.gradle.kts gradle.properties build.gradle.kts $APP_HOME

COPY /webapp $APP_HOME/webapp
RUN ./gradlew webapp:assemble

FROM gradle:8.10.2 AS db-processor
WORKDIR /app

COPY settings.gradle.kts gradle.properties build.gradle.kts /app/
COPY webapp/build.gradle.kts /app/webapp/build.gradle.kts

COPY webapp/src/main/resources/db/initDB.sql /app/webapp/src/main/resources/db/initDB.sql

RUN gradle webapp:processResources

FROM postgres:18-alpine AS db

COPY --from=db-processor /app/webapp/build/resources/main/db/initDB.sql /docker-entrypoint-initdb.d

FROM node:24-alpine AS frontend
WORKDIR /app

COPY frontend/package.json /app/package.json
COPY frontend/package-lock.json /app/package-lock.json
COPY frontend/tsconfig.json /app/tsconfig.json
COPY frontend/vite.config.ts /app/vite.config.ts
COPY frontend/index.html /app/index.html

RUN npm ci

COPY frontend/public /app/public
COPY frontend/src /app/src

RUN npm run build

FROM eclipse-temurin:21 AS pre-built
WORKDIR /app

EXPOSE 8080

COPY webapp/build/libs/*.jar /app/jars/*.jar
COPY frontend/dist/ /app/frontend/

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]

FROM eclipse-temurin:21 AS pre-built-complete
WORKDIR /app

EXPOSE 8080

COPY webapp/build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]

FROM eclipse-temurin:21 AS final
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/webapp/build/libs/*.jar /app/jars/*.jar
COPY --from=frontend /app/dist/ /app/frontend/

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]
