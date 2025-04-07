package com.lockbox.gateway.security;

import com.lockbox.gateway.security.auth.converter.CustomJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

/**
 * Configuration class for Spring Security in a reactive WebFlux environment.
 * <p>
 * This class configures the security settings for the application using Spring Security's WebFlux capabilities.
 * It includes configuration for OAuth2 login, JWT-based resource server protection, and access control for different
 * URL patterns. The configuration is primarily driven by a custom JWT authentication converter,
 * {@link CustomJwtAuthenticationConverter},
 * which maps JWT claims to Spring Security authentication tokens.
 * </p>
 *
 * <p>
 * The following security features are configured:
 * </p>
 * <ul>
 *     <li>OAuth2 login for handling user authentication using Keycloak</li>
 *     <li>JWT validation and mapping using {@link CustomJwtAuthenticationConverter}</li>
 *     <li>Access control rules for API endpoints
 *     (permit-all for Swagger UI and signup, authentication required for other endpoints)</li>
 *     <li>CSRF protection and HTTP Basic login are disabled</li>
 *     <li>Custom authentication success and failure handlers for OAuth2 login</li>
 *     <li>Custom exception handling for unauthorized access</li>
 * </ul>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * SecurityWebFilterChain securityChain = securityConfiguration.securityWebFilterChain(http);
 * }
 * </pre>
 *
 * @author serezk4
 * @version 1.0
 * @see CustomJwtAuthenticationConverter
 * @see ReactiveClientRegistrationRepository
 * @since 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    /**
     * The URI for the JWT set containing public keys for verifying JWT tokens.
     */
    private final String jwtSetUri;

    /**
     * The reactive client registration repository that provides client registrations for OAuth2 login.
     */
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    /**
     * Custom converter that maps JWT claims to Spring Security authentication tokens.
     */
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    public SecurityConfiguration(
            @Value("${spring.security.oauth2.client.provider.keycloak.jwk-set-uri}") String jwtSetUri,
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            CustomJwtAuthenticationConverter customJwtAuthenticationConverter
    ) {
        this.jwtSetUri = jwtSetUri;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
    }

    @Bean
    public SecurityWebFilterChain publicSecurityWebFilterChain(
            final ServerHttpSecurity http
    ) {
        return http
                // Match unprotected endpoints
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                        "/accounts/signup",
                        "/accounts/signup/**",
                        "/accounts/mail/send",
                        "/accounts/introspect/*",
                        "/boxes/access/open",
                        "/actuator/**",
                        "/avatars/get"
                ))
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method defines the access rules for different URL patterns, enables OAuth2 login, configures
     * JWT-based authentication for OAuth2 resource server, and disables CSRF and HTTP basic login.
     * </p>
     *
     * @param http the {@link ServerHttpSecurity} object used to configure security
     * @return the {@link SecurityWebFilterChain} object representing the configured security filter chain
     */
    @Bean
    public SecurityWebFilterChain protectedSecurityWebFilterChain(
            final ServerHttpSecurity http
    ) {
        return http
                // Match all requests
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/**"))

                // Authorization settings
                .authorizeExchange(exchanges -> exchanges
                        // Allow OPTIONS requests for CORS pre-flight
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        // Require authentication for all other requests
                        .anyExchange().authenticated()
                )

                // OAuth2 login configuration
                .oauth2Login(oauth2 -> oauth2
                        .clientRegistrationRepository(clientRegistrationRepository)
                        .authenticationSuccessHandler((webFilterExchange, authentication) ->
                                Mono.fromRunnable(() -> webFilterExchange
                                        .getExchange()
                                        .getResponse()
                                        .setStatusCode(HttpStatus.OK)
                                )
                        )
                        .authenticationFailureHandler((webFilterExchange, exception) ->
                                Mono.fromRunnable(() -> webFilterExchange
                                        .getExchange()
                                        .getResponse()
                                        .setStatusCode(HttpStatus.UNAUTHORIZED)
                                )
                        )
                )

                // OAuth2 resource server configuration with JWT validation
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                                    jwt.jwkSetUri(jwtSetUri);
                                    jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter);
                                }
                        )
                )

                // Disable CSRF, HTTP Basic, and form login
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // Custom exception handling for unauthorized access
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        )
                )
                .build();
    }
}
