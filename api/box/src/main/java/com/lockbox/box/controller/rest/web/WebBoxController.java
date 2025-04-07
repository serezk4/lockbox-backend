package com.lockbox.box.controller.rest.web;

import com.lockbox.box.controller.response.ApiResponse;
import com.lockbox.box.controller.response.web.box.BoxListResponse;
import com.lockbox.box.controller.response.web.box.BoxSignupResponse;
import com.lockbox.box.database.dto.BoxDto;
import com.lockbox.box.database.dto.BoxStatusDto;
import com.lockbox.box.database.dto.BoxWithStatusDto;
import com.lockbox.box.database.mapper.BoxMapper;
import com.lockbox.box.database.mapper.BoxStatusMapper;
import com.lockbox.box.database.mapper.BoxWithStatusMapper;
import com.lockbox.box.database.model.BoxSignup;
import com.lockbox.box.database.service.BoxService;
import com.lockbox.box.database.service.BoxSignupService;
import com.lockbox.box.database.service.BoxStatusService;
import com.lockbox.box.database.service.BoxWithStatusService;
import com.lockbox.box.security.auth.model.CustomUserDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * REST controller for managing lock boxes through web interfaces. Provides endpoints to list boxes,
 * sign up new boxes, retrieve box details, update box information, obtain box status, and delete boxes.
 * <p>
 * This controller ensures that all operations are performed by authenticated users and that users can only
 * manage their own lock boxes.
 * </p>
 *
 * <p><strong>Endpoint Base Path:</strong> <code>/web</code></p>
 *
 * <p><strong>Supported Operations:</strong></p>
 * <ul>
 *     <li>List all boxes owned by the authenticated user.</li>
 *     <li>Sign up a new box.</li>
 *     <li>Retrieve box details by MAC address.</li>
 *     <li>Edit box information.</li>
 *     <li>Get the latest status of a box.</li>
 *     <li>Delete (unassign) a box.</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class WebBoxController {
    BoxService boxService;
    BoxStatusService boxStatusService;
    BoxSignupService boxSignupService;
    BoxWithStatusService boxWithStatusService;

    BoxMapper boxMapper;
    BoxWithStatusMapper boxWithStatusMapper;
    BoxStatusMapper boxStatusMapper;

    /**
     * Retrieves a list of all lock boxes owned by the authenticated user.
     * <p>
     * This endpoint fetches all lock boxes associated with the authenticated user's subscription identifier.
     * The response includes a list of {@link BoxDto} objects encapsulated within an {@link ApiResponse.Body}.
     * </p>
     *
     * @param principal the authenticated user's details, used to identify owned boxes
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping a {@link BoxListResponse}
     * @throws RuntimeException if an error occurs while fetching the list of boxes
     */
    @GetMapping
    public Mono<ResponseEntity<ApiResponse.Body<BoxListResponse>>> list(
            @AuthenticationPrincipal final CustomUserDetails principal,

            @RequestParam(value = "page", defaultValue = "0")
            @Min(value = 0, message = "page.min:0") final int page,

            @Max(value = 10, message = "size.max:10")
            @Min(value = 1, message = "size.min:1")
            @RequestParam(value = "size", defaultValue = "10") final int size
    ) {
        return boxService.countAllByOwnerSub(principal.getSub())
                .flatMap(totalBoxes -> boxWithStatusService.findAllByOwnerSub(principal.getSub(), page, size)
                        .map(boxWithStatusMapper::toDto)
                        .collectList()
                        .map(boxes -> new BoxListResponse(boxes, totalBoxes, page, boxes.size())))
                        .map(ApiResponse::ok);
    }

    /**
     * Signs up a new lock box for the authenticated user.
     * <p>
     * This endpoint allows the authenticated user to register a new lock box. Upon successful registration,
     * a {@link BoxSignupResponse} containing a unique signup code is returned.
     * </p>
     *
     * @param principal the authenticated user's details, used to associate the new box signup
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping a {@link BoxSignupResponse}
     * @throws RuntimeException if the box signup process fails
     */
    @PostMapping("/signup")
    public Mono<ResponseEntity<ApiResponse.Body<BoxSignupResponse>>> signup(
            final @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return Mono.just(principal.getSub())
                .map(BoxSignup::new)
                .flatMap(boxSignupService::save)
                .flatMap(_box -> boxSignupService.findById(_box.getId()))
                .switchIfEmpty(Mono.error(new RuntimeException("box.signup.failed")))
                .map(_boxSignup -> new BoxSignupResponse(_boxSignup.getCode()))
                .map(ApiResponse::ok);
    }

    /**
     * Retrieves the details of a specific lock box by its MAC address.
     * <p>
     * This endpoint fetches the details of a lock box identified by its MAC address, ensuring that the box
     * is owned by the authenticated user. The response includes a {@link BoxDto} object encapsulated
     * within an {@link ApiResponse.Body}.
     * </p>
     *
     * @param macAddress the MAC address of the lock box to retrieve
     * @param principal  the authenticated user's details, used to verify ownership of the box
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping a {@link BoxWithStatusDto}
     * @throws RuntimeException if the box is not found or does not belong to the authenticated user
     */
    @GetMapping("/{macAddress}")
    public Mono<ResponseEntity<ApiResponse.Body<BoxWithStatusDto>>> getBoxByMacAddress(
            final @PathVariable String macAddress,
            final @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return boxWithStatusService.findByOwnerSubAndMacAddress(principal.getSub(), macAddress)
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.found")))
                .map(boxWithStatusMapper::toDto)
                .map(ApiResponse::ok);
    }

    /**
     * Edits the information of a specific lock box identified by its MAC address.
     * <p>
     * This endpoint allows the authenticated user to update details of a lock box, such as its alias or
     * other editable fields. The update is applied only if the box is owned by the authenticated user.
     * </p>
     *
     * @param macAddress the MAC address of the lock box to edit
     * @param boxDto     the data transfer object containing the updated box information
     * @param principal  the authenticated user's details, used to verify ownership of the box
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping the updated {@link BoxDto}
     * @throws RuntimeException if the box is not found, does not belong to the authenticated user,
     *                          or if an error occurs during the update
     */
    @PatchMapping("/{macAddress}")
    public Mono<ResponseEntity<ApiResponse.Body<BoxDto>>> editBox(
            final @PathVariable String macAddress,
            final @RequestBody BoxDto boxDto,
            final @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return boxService.findByMacAddress(macAddress)
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.found")))
                .filter(box -> box.getOwnerSub().equals(principal.getSub()))
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.yours")))
                .map(box -> boxMapper.partialUpdate(boxDto, box))
                .flatMap(boxService::save)
                .map(boxMapper::toDto)
                .map(ApiResponse::ok);
    }

    /**
     * Retrieves the latest status of a specific lock box by its MAC address.
     * <p>
     * This endpoint fetches the most recent status information of a lock box, including battery level, open
     * status, and signal strength. It ensures that the box belongs to the authenticated user.
     * </p>
     *
     * @param principal  the authenticated user's details, used to verify ownership of the box
     * @param macAddress the MAC address of the lock box whose status is to be retrieved
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping a {@link BoxStatusDto}
     * @throws RuntimeException if the box status is not found or if the box does not belong to the
     *                          authenticated user
     */
    @GetMapping("/{macAddress}/status")
    public Mono<ResponseEntity<ApiResponse.Body<BoxStatusDto>>> getBoxStatus(
            final @AuthenticationPrincipal CustomUserDetails principal,
            final @PathVariable String macAddress
    ) {
        return boxService.existsByMacAddressAndOwnerSub(macAddress, principal.getSub())
                .flatMap(boxStatusService::findTopByMacAddress)
                .map(boxStatusMapper::toDto)
                .map(ApiResponse::ok)
                .onErrorReturn(ApiResponse.bad("box.status.not.found"));
    }

    /**
     * Deletes (unassigns) a specific lock box identified by its MAC address.
     * <p>
     * This endpoint allows the authenticated user to delete a lock box, effectively unassigning ownership.
     * The operation is performed only if the box is currently owned by the authenticated user.
     * </p>
     *
     * @param macAddress the MAC address of the lock box to delete
     * @param principal  the authenticated user's details, used to verify ownership of the box
     * @return a reactive {@link Mono} containing a {@link ResponseEntity} with an
     * {@link ApiResponse.Body} wrapping the updated {@link BoxDto}
     * @throws RuntimeException if the box is not found, does not belong to the authenticated user,
     *                          or if an error occurs during the deletion
     */
    @DeleteMapping("/{macAddress}")
    public Mono<ResponseEntity<ApiResponse.Body<BoxDto>>> deleteBox(
            final @PathVariable String macAddress,
            final @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return boxService.findByMacAddress(macAddress)
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.found")))
                .filter(box -> box.getOwnerSub() != null && box.getOwnerSub().equals(principal.getSub()))
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.yours")))
                .map(_box -> {
                    _box.setOwnerSub(null);
                    return _box;
                })
                .flatMap(boxService::save)
                .map(boxMapper::toDto)
                .map(ApiResponse::ok);
    }
}
