plugins {
    id("java")
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "unspecified"

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
    implementation("com.h2database:h2:2.2.222")
    implementation("org.apache.commons:commons-compress:1.27.1")
    implementation("org.springframework.kafka:spring-kafka:3.2.3")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.2.3")

        // Testcontainers dependencies
        testImplementation("org.testcontainers:junit-jupiter:1.19.7")
        testImplementation("org.testcontainers:kafka:1.20.1")
        testImplementation("org.testcontainers:postgresql:1.20.1")

}

springBoot {
    mainClass.set("org.example.LogIngestionApplication")
}

// Task for unit tests only
tasks.register<Test>("unitTest") {
    description = "Runs only unit tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("unit") // Include only unit tests
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Task for integration tests only
tasks.register<Test>("integrationTest") {
    description = "Runs only integration tests."
    group = "verification"
    useJUnitPlatform {
        includeTags("integration") // Include only integration tests
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named("check") {
    dependsOn("unitTest") // Ensure check runs only unit tests
}
