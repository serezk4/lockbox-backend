package com.box.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the User Service Spring Boot application.
 * <p>
 * This class is responsible for bootstrapping the Spring Boot application. It uses the {@link SpringBootApplication}
 * annotation, which serves as a convenience annotation that combines {@code @Configuration},
 * {@code @EnableAutoConfiguration}, and {@code @ComponentScan} with default attributes.
 * <p>
 * The {@code scanBasePackages} parameter is used to specify the base package ("com.box.user") to scan for Spring
 * components, configuration classes, and services.
 * </p>
 * <p><b>Usage:</b></p>
 * This class contains the {@code main} method, which serves as the entry point for running the application.
 * When the application is started, Spring Boot initializes the Spring context, auto-configures beans, and starts
 * any embedded servers, such as Tomcat or Jetty.
 *
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication(scanBasePackages = "com.box.user")
public class UserServiceApplication {
    /**
     * The main method, serving as the entry point for the Spring Boot application.
     * <p>
     * This method delegates to {@link SpringApplication#run} to launch the application.
     * </p>
     */
    public static void main(String... args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
