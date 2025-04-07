package com.lockbox.box.controller.rest.web;

import com.lockbox.box.controller.request.web.access.ShareAccessRequest;
import com.lockbox.box.controller.response.ApiResponse;
import com.lockbox.box.database.dto.BoxAccessDto;
import com.lockbox.box.database.mapper.BoxAccessMapper;
import com.lockbox.box.database.model.BoxAccess;
import com.lockbox.box.database.service.BoxAccessService;
import com.lockbox.box.database.service.BoxService;
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

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing access to lock boxes. Provides endpoints to share access,
 * revoke access, and list current access permissions for a specific lock box.
 * <p>
 * This controller leverages reactive programming paradigms to handle requests asynchronously.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/access")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class AccessController {
    BoxService boxService;
    BoxAccessService boxAccessService;
    BoxAccessMapper boxAccessMapper;

    /**
     * Shares access to a lock box by creating a new access entry.
     * <p>
     * This endpoint allows the owner of a lock box to share access with another user by specifying
     * the MAC address of the box and the access duration.
     * </p>
     *
     * @param request     the request containing the MAC address and access time details
     * @param userDetails the authenticated user's details
     * @return a reactive Mono containing the API response with the created access details
     */
    @PostMapping("/{macAddress}/accesses")
    public Mono<ResponseEntity<ApiResponse.Body<BoxAccessDto>>> shareAccess(
            @NotBlank(message = "mac.address.not_blank")
            @NotNull(message = "mac.address.not_null")
            @Pattern(
                    regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
                    message = "mac.address.invalid_format"
            )
            @PathVariable final String macAddress,

            @RequestBody @Validated final ShareAccessRequest request,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        return boxService.findByMacAddress(macAddress)
                .filter(box -> box.getOwnerSub().equals(userDetails.getSub()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("box.not.found")))
                .map(box -> BoxAccess.builder()
                        .macAddress(macAddress)
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .build())
                .flatMap(boxAccessService::save)
                .map(boxAccessMapper::toDto)
                .map(ApiResponse::ok);
    }

    /**
     * Revokes access to a lock box by deleting the specified access entry.
     * <p>
     * This endpoint allows the owner of a lock box to revoke previously shared access using the access UUID.
     * </p>
     *
     * @param uuid        the UUID of the access entry to be revoked
     * @param userDetails the authenticated user's details
     * @return a reactive Mono containing the API response confirming the revocation
     * @throws IllegalArgumentException if the access entry is not found or the user is unauthorized
     */
    @DeleteMapping("/{macAddress}/accesses/{uuid}")
    public Mono<ResponseEntity<ApiResponse.Body<String>>> revokeAccess(
            @PathVariable("macAddress") String macAddress,
            @PathVariable("uuid") UUID uuid,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return boxService.findByMacAddress(macAddress)
                .filter(box -> box.getOwnerSub().equals(userDetails.getSub()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("box.not.found")))
                .flatMap(box -> boxAccessService.findById(uuid)
                        .filter(access -> access.getMacAddress().equals(macAddress))
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("access.not.found"))))
                .flatMap(access -> boxAccessService.deleteById(uuid))
                .then(Mono.just(ApiResponse.ok("access.revoked")));
    }

    /**
     * Retrieves a list of all access entries for a specific lock box.
     * <p>
     * This endpoint allows the owner of a lock box to view all current access permissions associated with the box.
     * </p>
     *
     * @param macAddress  the MAC address of the lock box
     * @param userDetails the authenticated user's details
     * @return a reactive Mono containing the API response with the list of access entries
     * @throws IllegalArgumentException if the lock box is not found or the user is unauthorized
     */
    @GetMapping("/{macAddress}/accesses")
    public Mono<ResponseEntity<ApiResponse.Body<List<BoxAccessDto>>>> listAccess(
            final @PathVariable("macAddress") String macAddress,
            final @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return boxService.findByMacAddress(macAddress)
                .filter(_box -> _box.getOwnerSub().equals(userDetails.getSub()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("box.not.found")))
                .flatMapMany(_box -> boxAccessService.findAllByMacAddress(macAddress))
                .map(boxAccessMapper::toDto)
                .collectList()
                .map(ApiResponse::ok);
    }
}
