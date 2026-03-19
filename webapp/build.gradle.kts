buildscript {
    extra["databaseDriver"] = "org.postgresql.Driver"
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:12+")
    }
}

plugins {
    id("org.jooq.jooq-codegen-gradle") version "3.19.30" // Make sure to synchronize this with the version of the jOOQ dependency used by Spring
    id("org.flywaydb.flyway") version "12.0.2"
    id("io.freefair.lombok") version "8.14.2"
}

dependencies {

    /* Spring Boot */
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    /* Swagger */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    /* Database */
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:12+")
    runtimeOnly("org.postgresql:postgresql:42.+")
    jooqCodegen("org.postgresql:postgresql:42.+")

    /* Image processing */
    implementation("org.openpnp:opencv:4.9.0-0")

    /* Miscellaneous */
    compileOnly("org.jetbrains:annotations:26.+")

}

/* Test suites */
@Suppress("UnstableApiUsage")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())

                implementation("org.springframework.boot:spring-boot-starter-webmvc-test")
                implementation("org.springframework.security:spring-security-test")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

sourceSets {
    main {
        java.srcDir("build/generated-src/jooq/main")
    }
    named("integrationTest") {
        compileClasspath += project.sourceSets.main.get().output
        runtimeClasspath += project.sourceSets.main.get().output
    }
}

@Suppress("UnstableApiUsage")
configurations {
    "integrationTestImplementation" {
        extendsFrom(configurations["implementation"])
    }
    "integrationTestRuntimeOnly" {
        extendsFrom(configurations["runtimeOnly"])
    }
}

/* Tasks */
tasks {
    jar {
        enabled = false
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<ProcessResources> {
        filesMatching(listOf("**/application.properties", "**/initDB.sql")) {
            expand(project.properties)
            expand(mapOf(
                "version" to project.version,
            ))

            //Replace "#[" with "${" and "]#" with "}", to resolve conflicts between Spring and Gradle
            filter { it.replace(Regex("#\\[(.*)]#"), "\\\${$1}") }
        }
    }

    compileJava {
        dependsOn(jooqCodegen)
    }

    jooqCodegen {
        dependsOn(flywayMigrate)
    }
}

/* Database configuration (from Gradle properties) */
val databaseUrl: String by project
val databaseName: String by project
val databaseUsername: String by project
val databasePassword: String by project
val databaseDriver: String by project
val databaseSchema: String by project

flyway {
    url = databaseUrl + databaseName
    user = databaseUsername
    password = databasePassword
    driver = databaseDriver
    schemas = arrayOf(databaseSchema)
}

jooq {
    configuration {
        jdbc {
            driver = databaseDriver
            url = databaseUrl + databaseName
            user = databaseUsername
            password = databasePassword
        }
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                excludes = "flyway_schema_history"
                inputSchema = databaseSchema
            }
            target {
                packageName = "org.jooq.generated"
                directory = "build/generated-src/jooq/main"
            }
        }
    }
}
