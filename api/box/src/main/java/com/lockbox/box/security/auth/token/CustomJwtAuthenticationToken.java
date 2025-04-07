package com.lockbox.box.security.auth.token;

import com.lockbox.box.security.auth.model.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

/**
 * Custom implementation of {@link JwtAuthenticationToken} that encapsulates additional user details.
 * <p>
 * This class extends {@link JwtAuthenticationToken} by adding a custom user details object, {@link CustomUserDetails},
 * which contains extra information about the authenticated user beyond the standard JWT claims. It also overrides the
 * {@link #getPrincipal()} method to return the custom user details object as the principal.
 * </p>
 *
 * <p>
 * This class is useful for incorporating custom user information into the authentication flow while still leveraging
 * Spring Security's support for JWT authentication.
 * </p>
 *
 * <ul>
 *     <li>{@code getPrincipal()} - Returns the {@link CustomUserDetails} object as the principal</li>
 * </ul>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * .oauth2ResourceServer(oauth2 -> oauth2
 *         .jwt(jwt -> {
 *                     jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter);
 *                 }
 *         )
 * )
 * }
 * </pre>
 *
 * @author serezk4
 * @version 1.0
 * @see JwtAuthenticationToken
 * @see CustomUserDetails
 * @since 1.0
 */
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    /**
     * Custom user details containing additional information about the authenticated user.
     */
    private final CustomUserDetails customUserDetails;

    /**
     * Constructs a {@link CustomJwtAuthenticationToken} with the given JWT, authorities, and custom user details.
     *
     * @param jwt               the {@link Jwt} containing the token claims
     * @param authorities       the granted authorities associated with the user
     * @param customUserDetails the custom user details object containing additional user information
     */
    public CustomJwtAuthenticationToken(
            Jwt jwt, Collection<? extends GrantedAuthority> authorities,
            CustomUserDetails customUserDetails
    ) {
        super(jwt, authorities, customUserDetails.getPreferredUsername());
        this.customUserDetails = customUserDetails;
    }

    /**
     * Returns the {@link CustomUserDetails} object as the principal for this authentication token.
     *
     * @return the {@link CustomUserDetails} representing the authenticated user
     */
    @Override
    public Object getPrincipal() {
        return this.customUserDetails;
    }
}
