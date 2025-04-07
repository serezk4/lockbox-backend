package com.lockbox.avatar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Avatar Service application.
 *
 * <p>This service is responsible for handling avatar-related operations,
 * including generating presigned URLs for secure file uploads and downloads.</p>
 *
 * <p>Built using Spring Boot, this application leverages auto-configuration
 * and component scanning to initialize required beans and services.</p>
 *
 * <p>Key features of this service:</p>
 * <ul>
 *     <li>Provides secure presigned URLs for avatar storage operations.</li>
 *     <li>Integrates with cloud storage solutions for efficient media management.</li>
 *     <li>Ensures security through authentication and access control mechanisms.</li>
 *     <li>Designed as a microservice, allowing for easy scalability and maintainability.</li>
 * </ul>
 *
 * <p>The application is bootstrapped by Spring Boot's {@link SpringApplication#run(Class, String...)}
 * method, which initializes the context and starts the embedded web server.</p>
 */
@SpringBootApplication(scanBasePackages = "com.lockbox.avatar")
public class AvatarServiceApplication {

    /**
     * Main method to launch the Avatar Service application.
     *
     * <p>Uses Spring Boot's {@link SpringApplication} to start the application,
     * performing component scanning and loading necessary configurations.</p>
     *
     * @param args Command-line arguments passed during application startup.
     */
    public static void main(String... args) {
        SpringApplication.run(AvatarServiceApplication.class, args);
    }
}
