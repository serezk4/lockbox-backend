package com.lockbox.flat.security.auth.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Represents custom user details used for authentication and authorization purposes.
 * <p>
 * This class contains various attributes related to the user's identity, roles, and token metadata,
 * commonly used in JWT (JSON Web Token) claims. It stores details like the user's unique identifier,
 * email verification status, roles, and additional metadata such as token expiration and issuance time.
 * </p>
 *
 * <ul>
 *     <li>{@code sub} - The subject or unique identifier of the user</li>
 *     <li>{@code emailVerified} - Indicates whether the user's email is verified</li>
 *     <li>{@code allowedOrigins} - A list of allowed origins for the user</li>
 *     <li>{@code roles} - A list of roles assigned to the user</li>
 *     <li>{@code issuer} - The issuer of the token</li>
 *     <li>{@code preferredUsername} - The user's preferred username</li>
 *     <li>{@code givenName} - The user's given name (first name)</li>
 *     <li>{@code familyName} - The user's family name (last name)</li>
 *     <li>{@code sid} - The session ID for the user</li>
 *     <li>{@code acr} - Authentication context class reference</li>
 *     <li>{@code azp} - Authorized party, typically the client that requested the token</li>
 *     <li>{@code scope} - The scope of access allowed for the user</li>
 *     <li>{@code name} - Full name of the user</li>
 *     <li>{@code email} - The email address of the user</li>
 *     <li>{@code exp} - Expiration time of the token in seconds since the epoch</li>
 *     <li>{@code iat} - Issued-at time of the token in seconds since the epoch</li>
 *     <li>{@code jti} - Unique identifier for the token</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@ToString
public class CustomUserDetails {

    /**
     * The subject or unique identifier of the user.
     */
    String sub;

    /**
     * Indicates whether the user's email is verified.
     */
    boolean emailVerified;

    /**
     * A list of allowed origins for the user.
     */
    List<String> allowedOrigins;

    /**
     * A list of roles assigned to the user.
     */
    List<String> roles;

    /**
     * The issuer of the token.
     */
    String issuer;

    /**
     * The user's preferred username.
     */
    String preferredUsername;

    /**
     * The user's given name (first name).
     */
    String givenName;

    /**
     * The user's family name (last name).
     */
    String familyName;

    /**
     * The session ID for the user.
     */
    String sid;

    /**
     * Authentication context class reference.
     */
    String acr;

    /**
     * Authorized party, typically the client that requested the token.
     */
    String azp;

    /**
     * The scope of access allowed for the user.
     */
    String scope;

    /**
     * Full name of the user.
     */
    String name;

    /**
     * The email address of the user.
     */
    String email;

    /**
     * Expiration time of the token in seconds since the epoch.
     */
    long exp;

    /**
     * Issued-at time of the token in seconds since the epoch.
     */
    long iat;

    /**
     * Unique identifier for the token.
     */
    String jti;
}
