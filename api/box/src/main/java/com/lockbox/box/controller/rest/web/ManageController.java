package com.lockbox.box.controller.rest.web;

import com.lockbox.box.controller.request.web.access.OpenByTokenRequest;
import com.lockbox.box.controller.response.ApiResponse;
import com.lockbox.box.database.dto.BoxUpdateDto;
import com.lockbox.box.database.mapper.BoxUpdateMapper;
import com.lockbox.box.database.model.BoxUpdate;
import com.lockbox.box.database.service.BoxAccessService;
import com.lockbox.box.database.service.BoxService;
import com.lockbox.box.database.service.BoxUpdateService;
import com.lockbox.box.security.auth.model.CustomUserDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing lock box operations such as opening boxes through tokens or authenticated requests.
 * <p>
 * This controller provides endpoints to open lock boxes either by using a token or through authenticated user requests.
 * Each operation results in the creation of a {@link BoxUpdate} entry to log the action performed.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class ManageController {
    BoxAccessService accessService;
    BoxUpdateService boxUpdateService;
    BoxService boxService;

    BoxUpdateMapper boxUpdateMapper;

    /**
     * Opens a lock box using a provided access token.
     * <p>
     * This endpoint allows users to open a lock box by supplying a valid access token. Upon successful validation,
     * a {@link BoxUpdate} entry is created to log the "open" action.
     * </p>
     *
     * @param request the request containing the access token to open the box {@link OpenByTokenRequest}
     * @return a reactive {@link Mono} containing the API response with the details of the box update
     * @throws IllegalArgumentException if the access token is invalid or does not correspond to any access
     */
    @PostMapping("/access/open")
    public Mono<ResponseEntity<ApiResponse.Body<BoxUpdateDto>>> openBox(
            final @RequestBody OpenByTokenRequest request
    ) {
        return accessService.findByToken(request.getToken())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("box.access.invalid.token")))
                .flatMap(_access -> boxUpdateService.save(BoxUpdate.builder()
                        .call("open")
                        .issuerToken(_access.getUuid())
                        .macAddress(_access.getMacAddress())
                        .build()))
                .map(boxUpdateMapper::toDto)
                .map(ApiResponse::ok);
    }

    /**
     * Opens a lock box through an authenticated user request.
     * <p>
     * This endpoint allows authenticated users to open a lock box by providing the MAC address of the box.
     * It ensures that the requesting user is the owner of the specified lock box. Upon successful validation,
     * a {@link BoxUpdate} entry is created to log the "open" action.
     * </p>
     *
     * @param userDetails the authenticated user's details, used to verify ownership of the lock box
     * @param macAddress  the MAC address of the lock box to open
     * @return a reactive {@link Mono} containing the API response with the details of the box update
     * @throws IllegalArgumentException if the user is not the owner of the specified lock box or if the box
     *                                     is not found
     */
    @PostMapping("/auth/open/{macAddress}")
    public Mono<ResponseEntity<ApiResponse.Body<BoxUpdateDto>>> openBox(
            final @AuthenticationPrincipal CustomUserDetails userDetails,

            @NotNull(message = "mac.address.not_null")
            @NotBlank(message = "macAddress.not_blank")
            @Pattern(
                    regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
                    message = "macAddress.invalid_format"
            )
            @PathVariable("macAddress") final String macAddress
    ) {
        return boxService.findByMacAddress(macAddress)
                .filter(box -> box.getOwnerSub().equals(userDetails.getSub()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("box.access.fail")))
                .flatMap(_box -> boxUpdateService.save(BoxUpdate.builder()
                        .call("open")
                        .issuerSub(userDetails.getSub())
                        .macAddress(_box.getMacAddress())
                        .build()))
                .map(boxUpdateMapper::toDto)
                .map(ApiResponse::ok);
    }
}
