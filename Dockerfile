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

FROM postgres AS db

COPY --from=builder /app/build/resources/main/db/initDB.sql /docker-entrypoint-initdb.d

FROM eclipse-temurin:21 AS final
WORKDIR /app

EXPOSE 8080

COPY --from=builder /app/build/libs/*.jar /app/jars/*.jar

ENTRYPOINT ["java", "-jar", "/app/jars/*.jar"]
