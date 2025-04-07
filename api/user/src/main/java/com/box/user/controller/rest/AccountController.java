package com.box.user.controller.rest;

import com.box.user.controller.dto.UserRepresentationDto;
import com.box.user.controller.mapper.UserRepresentationMapper;
import com.box.user.controller.request.revoke.SessionRevokeRequest;
import com.box.user.controller.request.user.UserEditRequest;
import com.box.user.controller.response.ApiResponse;
import com.box.user.controller.response.user.UserDetailsResponse;
import com.box.user.controller.response.user.UserSecurityDetailsResponse;
import com.box.user.security.auth.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class AccountController {

    UsersResource users;
    RealmResource realm;

    UserRepresentationMapper userRepresentationMapper;

    @GetMapping("/me")
    public Mono<ResponseEntity<ApiResponse.Body<UserDetailsResponse>>> getInfo(
            final @AuthenticationPrincipal Mono<CustomUserDetails> userDetails
    ) {
        return userDetails
                .map(user -> ApiResponse.ok(new UserDetailsResponse(user)))
                .switchIfEmpty(errorResponse("user.error:not_found", 404));
    }

    @GetMapping("/sessions")
    public Mono<ResponseEntity<ApiResponse.Body<UserSecurityDetailsResponse>>> security(
            final @AuthenticationPrincipal Mono<CustomUserDetails> userDetails
    ) {
        return userDetails
                .flatMap(this::fetchUserResource)
                .map(userResource -> ApiResponse.ok(new UserSecurityDetailsResponse(
                        userResource.getUserSessions(),
                        userResource.credentials().stream()
                                .map(UserSecurityDetailsResponse.CredentialsDto::new)
                                .toList())))
                .switchIfEmpty(errorResponse("user.error:not_provided", 400))
                .onErrorResume(IllegalStateException.class,
                        e -> errorResponse("user.error:not_found", 404));
    }

    @DeleteMapping("/sessions")
    public Mono<ResponseEntity<ApiResponse.Body<String>>> revokeSession(
            @RequestBody @Valid SessionRevokeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Requested revoking session {} for user {}", request.getSessionId(), userDetails.getSub());
        return Mono.fromSupplier(() -> realm.users().get(userDetails.getSub()).getUserSessions())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(sessions -> log.info("Found {} sessions for user {}", sessions.size(), userDetails.getSub()))
                .flatMapIterable(sessions -> sessions)
                .filter(session -> request.getSessionId().equals(session.getId()))
                .doOnNext(session -> {
                    log.info("Revoking session {} for user {}", session.getId(), userDetails.getSub());
                    users.get(session.getUserId()).logout();
                })
                .collectList()
                .map(list -> {
                    String message = list.isEmpty() ? "session:not found" : "session:revoked";
                    return ApiResponse.ok(message);
                });
    }

    @PostMapping("/reset-password")
    public Mono<ResponseEntity<ApiResponse.Body<String>>> resetPassword(
            final @AuthenticationPrincipal Mono<CustomUserDetails> userDetails
    ) {
        return userDetails
                .flatMap(this::fetchUserResource)
                .flatMap(userResource -> Mono.fromCallable(() -> {
                    userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"), null);
                    return ApiResponse.ok("user.message:password_reset_email_sent");
                }))
                .switchIfEmpty(errorResponse("user.error:not_provided", 400))
                .onErrorResume(IllegalStateException.class,
                        e -> errorResponse("user.error:not_found", 404));
    }

    @PatchMapping("/me")
    public Mono<ResponseEntity<ApiResponse.Body<UserRepresentationDto>>> editInfo(
            final @AuthenticationPrincipal Mono<CustomUserDetails> userDetails,
            final @Valid @RequestBody UserEditRequest editRequest
    ) {
        return userDetails
                .flatMap(this::fetchUserResource)
                .flatMap(userResource -> Mono.fromCallable(() -> {
                    UserRepresentation representation = userResource.toRepresentation();
                    userRepresentationMapper.partialUpdate(editRequest, representation);

                    if (editRequest.getEmail() != null) {
                        representation.setEmailVerified(false);
                        representation.setRequiredActions(List.of("UPDATE_EMAIL"));
                    }

                    userResource.update(representation);
                    return ApiResponse.ok(userRepresentationMapper.toDto(userResource.toRepresentation()));
                }))
                .switchIfEmpty(errorResponse("user.error:not_provided", 400))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> errorResponse("user.error:invalid_data", 422))
                .onErrorResume(IllegalStateException.class,
                        e -> errorResponse("user.error:not_found", 404));
    }

    private Mono<UserResource> fetchUserResource(final CustomUserDetails user) {
        return Mono.fromCallable(() -> users.get(user.getSub()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(Mono.error(new IllegalStateException("user.error:not_found")));
    }

    private <T> Mono<ResponseEntity<ApiResponse.Body<T>>> errorResponse(
            final String errorCode,
            final int status
    ) {
        return Mono.just(ApiResponse.bad(errorCode, status));
    }
}
