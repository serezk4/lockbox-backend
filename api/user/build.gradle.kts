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

// client
dependencies {
    implementation("org.apache.httpcomponents:httpasyncclient:4.1.5")
    implementation("org.jboss.resteasy:resteasy-client:6.2.11.Final")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.2")
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

    implementation("org.keycloak:keycloak-admin-client:26.0.4") {
        exclude(group = "commons-io", module = "commons-io")
    }

    implementation("commons-io:commons-io:2.18.0")
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
