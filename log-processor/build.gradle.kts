plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("org.springframework.kafka:spring-kafka:3.2.3")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")

    // Database H2 for local testing
    implementation("com.h2database:h2:2.2.222")

    // Database PostgreSQL for local test and production
    implementation("org.postgresql:postgresql:42.6.0")

    // Testing dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.2.3")

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")

    // Testcontainers for integration tests
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:kafka:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.register<Test>("unitTest") {
    description = "Runs only unit tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("unit") // Tag unit tests with @Tag("unit")
    }
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs only integration tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("integration") // Tag integration tests with @Tag("integration")
    }
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.named("check") {
    dependsOn("unitTest", "integrationTest")
}

springBoot {
    mainClass.set("org.example.LogProcessorApplication")
}
