FROM eclipse-temurin:21.0.5_11-jdk-alpine AS builder
ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY gradle $APP_HOME/gradle
COPY gradlew $APP_HOME
COPY settings.gradle.kts gradle.properties build.gradle $APP_HOME

COPY /webapp $APP_HOME/webapp
RUN ./gradlew webapp:assemble

FROM gradle:8.10.2 AS db-processor
WORKDIR /app

COPY settings.gradle.kts gradle.properties build.gradle /app/
COPY webapp/build.gradle /app/webapp/build.gradle

COPY webapp/src/main/resources/db/initDB.sql /app/webapp/src/main/resources/db/initDB.sql

RUN gradle webapp:processResources

FROM postgres AS db

COPY --from=db-processor /app/webapp/build/resources/main/db/initDB.sql /docker-entrypoint-initdb.d

FROM eclipse-temurin:21 AS jar
WORKDIR /app

EXPOSE 8080

COPY webapp/build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]

FROM eclipse-temurin:21 AS final
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/webapp/build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]
