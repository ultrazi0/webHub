# Project Rexus

Project Rexus is a web application that allows users to manage and control their robots remotely (one such robot is called a "Rex").

---

## Setting up
It is possible to skip most of the setup by directly pulling the Docker images from the GitLab registry.
However, to be able to do so, you need to ask for credentials ;)

### 1. The database
Project Rexus uses Postgres. It is possible to set it up in three ways:

1. Pulling the pre-configured Docker image from the GitLab registry (should be used **only** for running locally or testing)
    - Username: `server`
    - Password: `server`
    - Database name: `rexus-db`
    - Schema: `bot`
2. Building the image using the docker-compose.yml file (the easiest way, but requires cloning the repo)
    - The following variables in the `gradle.properties` file must be set (refer to the example below):
      - `databaseName`
      - `databaseUrl`
      - `databaseUsername`
      - `databasePassword`
      - `databaseSchema`

3. Manually setting up the container
    - The `gradle.properties` file must still be configured if running the development server

### 2. Project Rexus
As with the database, there are three ways to get it running:

1. Pulling the Docker image from the GitLab registry (unlike the database, this is the best way to run the production server)
    - Requires configuring the database connection by providing the following environmental variables:
      - `DB_URL` (example: `jdbc:postgresql://db:5432/rexus-db?currentSchema=bot`)
      - `DB_USERNAME` (defaults to `server`)
      - `DB_PASSWORD` (defaults to `server`)
2. Building the image using the docker-compose.yml file (requires cloning the repo)
    - This still creates a production-ready image but does not require asking me for credentials ;)
    - The `gradle.properties` file must be configured
    - This option is kind of expecting that the database is also run using the `docker-compose.yml` file
3. Running locally (kind of expects IntelliJ, but any other IDE will suffice)
    - The `gradle.properties` file must be configured (refer to the database section, option 2 or to the example below)
    - In IntelliJ settings (Build, Execution, Deployment → Build Tools → Gradle),
make sure that the project is run with Gradle, as it requires the `processResources` task to be executed
    - Run the pre-configured `Rexus | BE` and `Rexus | FE` run configurations

### 3. The Rex client
Project Rexus manages robots, so, in order for the application to be useful, users must also set up the Rex client.
Ultimately, this task falls to the users of the application.

---
### Example of a `gradle.properties` file
```properties
databaseName = rexus-db
databaseUrl = jdbc:postgresql://localhost:5432/
databaseUsername = server
databasePassword = server
databaseSchema = bot
```
