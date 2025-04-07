plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("checkstyle")
    id("org.owasp.dependencycheck") version "11.1.0"
}

group = "com.lockbox"
version = "1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

// cloud
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.2.0")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.2.0")
}

// useful things
dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

// health
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
}

// cache
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

// json
dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

// validation
dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

// security
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
}

// web
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

// logging
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }

    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
}

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "ch.qos.logback", module = "logback-core")
}

// tests
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.check {
    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
}

dependencyCheck {
    nvd {
        apiKey = System.getenv("NVD_API_KEY") ?: ""
    }
}