package com.lockbox.avatar.security.auth.converter;

import com.lockbox.avatar.security.auth.model.CustomUserDetails;
import com.lockbox.avatar.security.auth.token.CustomJwtAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converter class that transforms a {@link Jwt} into a {@link Mono} of {@link AbstractAuthenticationToken}.
 * <p>
 * This class is responsible for converting JWT tokens into Spring Security authentication tokens, with
 * user details extracted and assigned to a custom {@link CustomJwtAuthenticationToken}. It implements
 * the {@link Converter} interface provided by Spring Security and works in a reactive context with {@link Mono}.
 * </p>
 *
 * <p>
 * The class extracts user details from the JWT claims such as subject, roles, and other attributes, and wraps them
 * in a {@link CustomUserDetails} object. The user's roles are mapped to {@link GrantedAuthority} instances.
 * </p>
 *
 * <ul>
 *     <li>{@code convert(Jwt jwt)} - Converts a {@link Jwt} into a {@link CustomJwtAuthenticationToken}
 *     containing user details</li>
 *     <li>{@code extractAuthorities(Jwt jwt)} - Extracts roles from the JWT and converts them into
 *     {@link GrantedAuthority} instances</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.1
 * @see Jwt
 * @see CustomJwtAuthenticationToken
 * @see CustomUserDetails
 * @see Converter
 * @since 1.0
 */
@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    /**
     * Converts the given JWT token into a custom authentication token.
     * <p>
     * It extracts the user details from the JWT claims and maps the roles to {@link GrantedAuthority} instances.
     * </p>
     *
     * @param jwt the {@link Jwt} token containing claims
     * @return a {@link Mono} of {@link AbstractAuthenticationToken} containing {@link CustomUserDetails}
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt jwt) {
        CustomUserDetails userDetails = new CustomUserDetails(
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsBoolean("email_verified"),
                jwt.getClaimAsStringList("allowed-origins"),
                jwt.getClaimAsStringList("roles"),
                jwt.getIssuer().toString(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"),
                jwt.getClaimAsString("sid"),
                jwt.getClaimAsString("acr"),
                jwt.getClaimAsString("azp"),
                jwt.getClaimAsString("scope"),
                jwt.getClaimAsString("name"),
                jwt.getClaimAsString("email"),
                Optional.ofNullable(jwt.getExpiresAt()).map(Instant::getEpochSecond).orElse(0L),
                Optional.ofNullable(jwt.getIssuedAt()).map(Instant::getEpochSecond).orElse(0L),
                jwt.getClaimAsString("jti")
        );

        return Mono.just(new CustomJwtAuthenticationToken(jwt, extractAuthorities(jwt), userDetails));
    }

    /**
     * Extracts the roles from the JWT's {@code realm_access} claim and converts them into {@link GrantedAuthority}.
     *
     * @param jwt the {@link Jwt} token containing the claims
     * @return a collection of {@link GrantedAuthority} representing the user's roles
     */
    private List<SimpleGrantedAuthority> extractAuthorities(final Jwt jwt) {
        return Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                .map(map -> map.get("roles"))
                .filter(Collection.class::isInstance)
                .map(roles -> ((Collection<?>) roles).stream()
                        .map(Object::toString)
                        .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
