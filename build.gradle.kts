plugins {
    id("org.springframework.boot") version "3.3.3" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("java")
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

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
        implementation("jakarta.validation:jakarta.validation-api:3.0.2")


        implementation("org.springframework.kafka:spring-kafka:3.2.7")
        implementation("com.h2database:h2:2.2.222")
        implementation("org.apache.commons:commons-compress:1.27.1")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        testImplementation("org.assertj:assertj-core:3.26.3")
        testImplementation("org.springframework.kafka:spring-kafka-test:3.2.4")
    }

    tasks.named("check") {
        dependsOn("test")
    }
}
