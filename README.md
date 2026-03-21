# Project Rexus

Project Rexus is a web application designed to manage and control robots remotely (one such robot is called a "Rex").

---

## Tech Stack

- **Backend:** Java 21, Spring Boot 4.0.3, Spring Security, Flyway, jOOQ
- **Frontend:** React 19, Vite, TypeScript, React Bootstrap, React Router 7, WebSockets
- **Database:** PostgreSQL
- **Build Tool:** Gradle (Backend), npm (Frontend)
- **Testing:** JUnit 5, Selenide (UI), RestAssured (API), JGiven (BDD), Flyway (Migrations)
- **Infrastructure:** Docker, Docker Compose

---

## Requirements

- **JDK 21** (Temurin recommended)
- **Node.js 24+** (for frontend development)
- **Docker & Docker Compose**
- **PostgreSQL** (if running locally without Docker)

---

## Project Structure

```text
rexus/
├── frontend/           # React frontend (Vite, TypeScript)
├── webapp/             # Spring Boot backend (Core logic, DB migrations)
├── testing/            # System and E2E tests (Selenide, RestAssured, JGiven)
├── gradle/             # Gradle wrapper and configuration
├── docker-compose.yml  # Local development infrastructure
└── Dockerfile          # Multi-stage build for backend, frontend, and DB
```

---

## Setup & Running

### 1. Database Setup

Project Rexus uses PostgreSQL. You can set it up in several ways:

#### Option A: Docker Compose (Recommended)
This is the easiest way to get everything running, including the database and the application.
1. **Configure `gradle.properties`**: Create or update `gradle.properties` in the root directory.
   ```properties
   databaseName = rexus-db
   databaseUrl = jdbc:postgresql://localhost:5432/
   databaseUsername = server
   databasePassword = server
   databaseSchema = bot
   ```
2. Run `./gradlew processResources`
3. Use Docker compose
   ```bash
   docker compose up --build
   ```

#### Option B: Manual PostgreSQL Setup
1. Create a database named `rexus-db`.
2. Configure a schema named `bot`.
3. Update `gradle.properties` with your credentials (see example below).

### 2. Running the Application

#### Local Development (IDE)
1. **Configure `gradle.properties`**. Should be already done in the previous step
2. **Backend**: Run the Spring Boot application (e.g., via IntelliJ `Rexus | BE` configuration or `./gradlew :webapp:bootRun`).
3. **Frontend**: Use the `Rexus | FE` run configurations or use commands:
   ```bash
   cd frontend
   npm install
   npm run start
   ```

#### Production (Docker)
Pull or build the Docker image. The application requires these environment variables for database connection:
- `DB_URL` (e.g., `jdbc:postgresql://db:5432/rexus-db?currentSchema=bot`)
- `DB_USERNAME` (defaults to `server`)
- `DB_PASSWORD` (defaults to `server`)

---

## Scripts & Commands

### Backend (Gradle)
- `./gradlew :webapp:assemble`: Build the backend JAR.
- `./gradlew :webapp:test`: Run unit tests.
- `./gradlew :webapp:integrationTest`: Run integration tests.
- `./gradlew :webapp:flywayMigrate`: Run database migrations.
- `./gradlew :webapp:jooqCodegen`: Generate jOOQ classes from the database schema.

### Frontend (npm)
- `npm run start`: Start Vite development server.
- `npm run build`: Build the frontend for production.
- `npm run check`: Run TypeScript type checking.
- `npm run preview`: Preview the production build locally.

### System Testing
- `./gradlew :testing:test`: Run E2E/System tests using Selenide and RestAssured. Generates a [JGiven report](https://ultrazi0.github.io/Rexus/).

---

## Environment Variables

| Variable      | Description             | Default  |
|---------------|-------------------------|----------|
| `DB_URL`      | JDBC URL for PostgreSQL | -        |
| `DB_USERNAME` | Database username       | `server` |
| `DB_PASSWORD` | Database password       | `server` |

---

## Tests

- **Unit Tests**: Located in `webapp/src/test`.
- **Integration Tests**: Located in `webapp/src/integrationTest`.
- **System Tests**: Located in `testing/src/test`. These include UI tests (Selenide) and API tests (RestAssured) with BDD reporting via JGiven.

---

## The Rex Client

Project Rexus manages robots. To make the application useful, users must also set up the **Rex client** on their robots.
Documentation for the client setup is [TODO: Add link or instructions].
