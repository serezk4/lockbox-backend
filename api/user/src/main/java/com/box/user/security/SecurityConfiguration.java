package com.box.user.security;

import com.box.user.security.auth.converter.CustomJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This configuration removes OAuth token validation and access rules but retains
 * JWT parsing to extract user details.
 *
 * @author serezk4
 * @version 2.0
 * @see CustomJwtAuthenticationConverter
 * @since 1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    public SecurityConfiguration(CustomJwtAuthenticationConverter customJwtAuthenticationConverter) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
    }

    /**
     * Configures a {@link ReactiveJwtDecoder} bean for decoding and verifying JWT tokens using the JWK Set URI.
     * <p>
     * The JWK Set URI is provided via the application properties under the key:
     * {@code spring.security.oauth2.client.provider.keycloak.jwk-set-uri}.
     * This URI typically points to the Keycloak's JWK endpoint, which provides the keys for verifying JWT signatures.
     * </p>
     * <p>
     * Example Keycloak JWK Set URI: {@code http://localhost:8080/realms/<realm>/protocol/openid-connect/certs}
     * </p>
     *
     * @param jwkSetUri the JWK Set URI from which the public keys for JWT verification are fetched.
     * @return a {@link ReactiveJwtDecoder} instance configured to decode JWTs using the provided JWK Set URI.
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            final @Value("${spring.security.oauth2.client.provider.keycloak.jwk-set-uri}") String jwkSetUri
    ) {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * Configures the {@link SecurityWebFilterChain} bean to define security settings for the application.
     * <p>
     * This configuration is tailored for a gateway application and includes the following:
     * <ul>
     *   <li>Disables CSRF protection, HTTP Basic authentication, and form-based login, as they are not necessary
     *   for a gateway.</li>
     *   <li>Configures the application to use JWT parsing via the OAuth2 resource server functionality.</li>
     *   <li>Applies a custom
     *   {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}
     *       for extracting additional claims or roles from the JWT token.</li>
     *   <li>Allows all requests to pass through without enforcing specific security policies.</li>
     * </ul>
     * </p>
     * <p>
     * This configuration is typically used when the gateway delegates authentication and authorization
     * to downstream services and does not enforce these policies itself.
     * </p>
     *
     * @param http the {@link ServerHttpSecurity} to customize the security configuration for WebFlux.
     * @return a fully configured {@link SecurityWebFilterChain} instance.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF, HTTP Basic, and form login as they're unnecessary for a gateway
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // Configure JWT parsing without enforcing security policies
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter))
                )
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll()) // Allow all requests
                .build();
    }
}

