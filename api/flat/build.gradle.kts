plugins {
    java
    id("org.springframework.boot") version "3.3.2"
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

// kafka
dependencies {
    implementation("org.springframework.kafka:spring-kafka:3.2.2")
}

// cloud
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.4")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.2")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

// documentation
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
}

// useful things
dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

// health
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
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

// database (postgres (r2dbc) & flyway for migrations & redis for caching)
dependencies {
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.15.0")
}

// security
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.3.3")
    implementation("org.keycloak:keycloak-admin-client:26.0.0")
}

// web
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("com.graphql-java:graphql-java-extended-scalars:22.0")
}

// tests
dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
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
