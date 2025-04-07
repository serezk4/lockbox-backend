package com.box.user.controller.client;

import com.box.user.controller.response.introspect.IntrospectTokenResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TokenIntrospectionService {
    WebClient webClient;
    String clientId;
    String clientSecret;

    public TokenIntrospectionService(
            final WebClient.Builder webClientBuilder,
            final @Value("${keycloak.realm-link}") String realmLink,
            final @Value("${keycloak.client-id}") String clientId,
            final @Value("${keycloak.client-secret}") String clientService
    ) {
        this.clientId = clientId;
        this.clientSecret = clientService;
        this.webClient = webClientBuilder.baseUrl(realmLink).build();
    }

    public Mono<IntrospectTokenResponse> introspectToken(final String token) {
        return webClient.post()
                .uri("/protocol/openid-connect/token/introspect")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(String.format(
                        "client_id=%s&client_secret=%s&token=%s",
                        clientId, clientSecret, token
                ))
                .retrieve()
                .bodyToMono(IntrospectTokenResponse.class);
    }
}
