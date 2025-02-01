FROM eclipse-temurin:21.0.5_11-jdk-alpine AS builder
ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY gradle $APP_HOME/gradle
COPY gradlew $APP_HOME
COPY settings.gradle gradle.properties build.gradle $APP_HOME

COPY ./src ./src
COPY ./testing/build.gradle $APP_HOME/testing/build.gradle
COPY ./testing/src/main $APP_HOME/testing/src/main
RUN ./gradlew assemble

FROM gradle AS db-processor
WORKDIR /app

COPY settings.gradle gradle.properties build.gradle /app/

COPY src/main/resources/db/initDB.sql src/main/resources/db/initDB.sql

RUN gradle processResources

FROM postgres AS db

COPY --from=db-processor /app/build/resources/main/db/initDB.sql /docker-entrypoint-initdb.d

FROM eclipse-temurin:21 AS final
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]

FROM eclipse-temurin:21 AS jar
WORKDIR /app

EXPOSE 8080

COPY /build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]
