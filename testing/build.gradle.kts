plugins {
    id("com.tngtech.jgiven.gradle-plugin") version "1.3.1"
    id("io.freefair.lombok") version "8.14.2"
}

dependencies {

    /* Spring Boot */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jooq")

    /* Database */
    testRuntimeOnly("org.postgresql:postgresql:42.+")

    /* JGiven */
    testImplementation("com.tngtech.jgiven:jgiven-spring-junit5:1.3.1")

    /* API testing */
    testImplementation("io.rest-assured:rest-assured:5.5.6")

    /* UI testing */
    testImplementation("com.codeborne:selenide:7.10.0")

}

sourceSets {
    test {
        val classpath = project(":webapp")
            .extensions
            .getByType<SourceSetContainer>()
            .main.get()
            .runtimeClasspath

        compileClasspath += classpath
        runtimeClasspath += classpath
    }
}

tasks {
    bootJar {
        enabled = false
    }
    jar {
        enabled = false
    }

    test {
        useJUnitPlatform()

        @Suppress("UNCHECKED_CAST")
        systemProperties = System.getProperties().filter { it.key != "library.jansi.path" } as Map<String, Any>

        finalizedBy(jgivenTestReport)
    }

    jgivenTestReport {
        reports {
            html.title = "Rexus System Report"
            html.customJsFile = projectDir.resolve("src/test/resources/jgiven/custom.js")
            html.customCssFile = projectDir.resolve("src/test/resources/jgiven/custom.css")
        }

        doLast {
            ant.withGroovyBuilder {
                "replaceregexp"(
                    "file" to "build/reports/jgiven/test/html/index.html",
                        "match" to "ng-init=\"step = scenarioCase\\.steps\\[\\\$parent\\.\\\$index\\]\"",
                        "replace" to "ng-init=\"step = getCaseStep(step, scenarioCase, \$parent)\""
                        )
            }
        }
    }
}
