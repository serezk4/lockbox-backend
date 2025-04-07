package com.box.user.controller.rest;

import com.box.user.controller.request.mail.SendMailCodeReqeust;
import com.box.user.controller.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;

/**
 * {@code MailController} is a REST controller responsible for handling email-related requests,
 * particularly sending verification emails to users.
 * <p>
 * This controller interacts with Keycloak's {@link UsersResource} to search for users by their email address
 * and to trigger the email verification process using Keycloak's built-in actions.
 * </p>
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>
 * POST /mail/send
 * Content-Type: application/json
 *
 * {
 *     "mail": "user@example.com"
 * }
 * </pre>
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @see UsersResource
 * @see ApiResponse
 * @since 1.4
 */
@RestController
@RequestMapping("/mail")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class MailController {

    /**
     * A reference to the Keycloak {@link UsersResource}, which is used to create and manage users.
     */
    UsersResource users;

    /**
     * Sends a verification email to the user with the specified email address.
     * <p>
     * This method first searches for users with the specified email address and filters out
     * those who have already verified their email. If a user is found, a verification email
     * is sent to them using Keycloak's built-in email actions.
     * </p>
     *
     * @param mail - The request containing the email address to send the verification email to.
     * @return A {@link Mono} emitting a {@link ResponseEntity} containing the API response.
     */
    @PostMapping("/send")
    public Mono<ResponseEntity<ApiResponse.Body<String>>> sendVerificationMail(
            final @PathParam("mail") String mail
    ) {
        return Mono.fromCallable(() -> {
                    List<UserRepresentation> foundUsers = users.search(mail);
                    log.info("Found {} user(s) for email: {}", foundUsers.size(), mail);
                    return foundUsers.stream()
                            .filter(user -> !user.isEmailVerified())
                            .findFirst();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalUser -> optionalUser.map(Mono::just).orElseGet(Mono::empty))
                .switchIfEmpty(Mono.error(new MailSendException("Invalid email or user already verified")))
                .flatMap(user ->
                        Mono.fromCallable(() -> users.get(user.getId()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .doOnSuccess(userResource -> {
                                    log.info("Sending verification email to user with ID: {}", user.getId());
                                    userResource.executeActionsEmail(Collections.singletonList("VERIFY_EMAIL"));
                                    log.info("Verification email sent to user with ID: {}", user.getId());
                                })
                )
                .doOnError(error -> log.error("Error during verification process for email {}: {}",
                        mail, error.getMessage(), error))
                .thenReturn(ApiResponse.ok("Sent"));
    }
}
