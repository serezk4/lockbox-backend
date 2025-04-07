package com.box.user.configuration.keycloak;

import lombok.extern.log4j.Log4j2;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for integrating Keycloak with a Spring application.
 * This class provides the necessary beans for connecting to a Keycloak server,
 * authenticating via client credentials, and obtaining access to a specific realm's resources.
 * <p>
 * The configuration retrieves Keycloak settings such as server URL, realm name, client ID,
 * client secret, and admin credentials from the application's properties file, which are then
 * used to build a {@link Keycloak} client. Additionally, it provides access to the Keycloak
 * realm resource through the {@link RealmResource} bean.
 * </p>
 *
 * <h2>Key Points:</h2>
 * <ul>
 *     <li>Retrieves configuration parameters using Spring's {@code @Value} annotation.</li>
 *     <li>Builds a {@link Keycloak} client with the client credentials grant type.</li>
 *     <li>Provides a {@link RealmResource} bean to interact with Keycloak realms.</li>
 * </ul>
 *
 * <p><b>Usage:</b></p>
 * This class is intended to be used in Spring-based applications that require integration
 * with Keycloak for user management and authentication purposes.
 *
 * @author serezk4
 * @version 1.0
 * @since 2.0
 */
@Configuration
@Log4j2
public class KeycloakConfiguration {
    /**
     * Creates and configures a {@link Keycloak} bean that connects to the Keycloak server.
     * This bean is responsible for interacting with Keycloak using client credentials
     * (client ID and client secret) and authenticates as an administrator using the
     * specified username and password.
     * <p>
     * This method uses the {@link KeycloakBuilder} to build a {@link Keycloak} instance with
     * the following parameters:
     * <ul>
     *     <li>{@code serverUrl} - The URL of the Keycloak server.</li>
     *     <li>{@code realm} - The name of the Keycloak realm to interact with.</li>
     *     <li>{@code clientId} - The client ID used for authentication.</li>
     *     <li>{@code clientSecret} - The client secret associated with the client ID.</li>
     *     <li>{@code username} - The administrator's username for authentication.</li>
     *     <li>{@code password} - The administrator's password for authentication.</li>
     * </ul>
     *
     * @param serverUrl    The base URL of the Keycloak server, typically in the format {@code http(s)://host:port/auth}
     * @param realm        The name of the Keycloak realm where the client is registered.
     * @param clientId     The client ID used for the OAuth2 client credentials grant type.
     * @param clientSecret The client secret corresponding to the client ID, used for authentication.
     * @param username     The Keycloak admin username to authenticate with.
     * @param password     The Keycloak admin password to authenticate with.
     * @return A {@link Keycloak} client instance that can be used to perform administrative operations on the realm.
     */
    @Bean
    public Keycloak keycloak(
            @Value("${keycloak.server.url}") final String serverUrl,
            @Value("${keycloak.realm}") final String realm,
            @Value("${keycloak.client-id}") final String clientId,
            @Value("${keycloak.client-secret}") final String clientSecret,
            @Value("${keycloak.admin.username}") final String username,
            @Value("${keycloak.admin.password}") final String password
    ) {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ResteasyClientBuilder.newBuilder();

        builder.maxPooledPerRoute(50)
                .useAsyncHttpEngine()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .resteasyClient(builder.build())
                .build();
    }

    /**
     * Provides a {@link RealmResource} bean that allows interacting with the specified realm in Keycloak.
     * <p>
     * This method fetches the realm resource using the {@link Keycloak} instance created in the previous bean.
     * The realm name used here is hardcoded as {@code "spring-app-realm"}, which may correspond to the realm
     * where the Spring application is registered in Keycloak.
     * </p>
     *
     * @param keycloak The {@link Keycloak} instance to use for retrieving the realm resource.
     * @return A {@link RealmResource} representing the specified realm in Keycloak.
     */
    @Bean("default-realm")
    public RealmResource realmResource(
            final Keycloak keycloak,
            @Value("${keycloak.realm}") final String realm
    ) {
        return keycloak.realm(realm);
    }

    /**
     * Provides a {@link UsersResource} bean that allows interacting with the users in the specified realm.
     * <p>
     * This method fetches the users resource using the {@link RealmResource} instance created in the previous bean.
     * The realm name used here is hardcoded as {@code "spring-app-realm"}, which may correspond to the realm
     * where the Spring application is registered in Keycloak.
     * </p>
     *
     * @param realm The {@link RealmResource} instance to use for retrieving the users resource.
     * @return A {@link UsersResource} representing the users in the specified realm in Keycloak.
     */
    @Bean("default-users")
    public UsersResource usersResource(
            final RealmResource realm
    ) {
        return realm.users();
    }
}
