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

    // H2 Database for testing
    implementation("com.h2database:h2:2.2.222")

    // Commons Compress library
    implementation("org.apache.commons:commons-compress:1.27.1")

    // Spring Kafka for message streaming
    implementation("org.springframework.kafka:spring-kafka:3.2.3")

    // Testing dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.assertj:assertj-core:3.24.2")

    // Kafka Test dependencies
    testImplementation("org.springframework.kafka:spring-kafka-test:3.2.3")

    // Conditionally include Testcontainers for local development
    if (!System.getenv().containsKey("CI")) {
        testImplementation("org.testcontainers:junit-jupiter:1.19.7")
        testImplementation("org.testcontainers:kafka:1.20.1")
        testImplementation("org.testcontainers:postgresql:1.20.1")
    }
}

springBoot {
    mainClass.set("org.example.LogIngestionApplication")
}

