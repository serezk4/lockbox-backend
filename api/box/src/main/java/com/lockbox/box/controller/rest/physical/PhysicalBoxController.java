package com.lockbox.box.controller.rest.physical;

import com.lockbox.box.controller.request.physical.box.BoxSignupRequest;
import com.lockbox.box.controller.response.ApiResponse;
import com.lockbox.box.controller.response.physical.box.BoxUpdatesResponse;
import com.lockbox.box.database.dto.BoxDto;
import com.lockbox.box.database.dto.BoxStatusDto;
import com.lockbox.box.database.mapper.BoxMapper;
import com.lockbox.box.database.mapper.BoxStatusMapper;
import com.lockbox.box.database.mapper.PublicBoxUpdateMapper;
import com.lockbox.box.database.model.Box;
import com.lockbox.box.database.model.BoxSignup;
import com.lockbox.box.database.service.BoxService;
import com.lockbox.box.database.service.BoxSignupService;
import com.lockbox.box.database.service.BoxStatusService;
import com.lockbox.box.database.service.BoxUpdateService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * REST controller for managing physical lock box operations.
 * <p>
 * This controller provides endpoints for physical devices to interact with lock boxes.
 * It supports operations such as registering a new lock box using a signup code,
 * updating the status of an existing lock box, and fetching updates for a specific lock box.
 * These operations are designed to be used by physical devices without requiring user authentication.
 * </p>
 *
 * <p><strong>Endpoint Base Path:</strong> <code>/physical/boxes</code></p>
 *
 * <p><strong>Supported Operations:</strong></p>
 * <ul>
 *     <li>Register a lock box using a signup code and MAC address.</li>
 *     <li>Update the status of an existing lock box.</li>
 *     <li>Fetch updates for a specific lock box based on its MAC address and a timestamp.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <pre>{@code
 * POST /physical/boxes/signup
 * {
 *     "code": "123456",
 *     "macAddress": "00:1A:2B:3C:4D:5E"
 * }
 *
 * PATCH /physical/boxes/{macAddress}/status
 * {
 *     "macAddress": "00:1A:2B:3C:4D:5E",
 *     "batteryLevel": 85.0,
 *     "signalStrength": -45,
 *     "opened": true
 * }
 *
 * GET /physical/boxes/{macAddress}/updates?afterTimestamp=2025-01-01T12:00:00Z
 * }</pre>
 *
 * @author serezk4
 * @version 1.1
 * @since 1.0
 */
@RestController
@RequestMapping("/physical/boxes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class PhysicalBoxController {

    BoxSignupService boxSignupService;
    BoxService boxService;
    BoxStatusService boxStatusService;
    BoxUpdateService boxUpdateService;

    BoxMapper boxMapper;
    BoxStatusMapper boxStatusMapper;
    PublicBoxUpdateMapper publicBoxUpdateMapper;

    /**
     * Registers (signs up) a new lock box using a provided signup code and MAC address.
     * <p>
     * This endpoint allows a client to associate a lock box with an owner by supplying a valid
     * signup code and the MAC address of the box. It validates the signup code and ensures that
     * the box is not already owned before assigning the owner to the box.
     * </p>
     *
     * <p><strong>Request Body:</strong></p>
     * The request body should contain a {@link BoxSignupRequest} with the following fields:
     * <ul>
     *     <li>{@code code} - The signup code associated with the owner. Must be valid and not expired.</li>
     *     <li>{@code macAddress} - The MAC address of the lock box to be registered. Must be in a valid MAC
     *     address format.</li>
     * </ul>
     *
     * <p><strong>Returns:</strong></p>
     * <ul>
     *     <li>A {@link BoxDto} representing the registered lock box with its updated owner information.</li>
     * </ul>
     *
     * <p><strong>Throws:</strong></p>
     * <ul>
     *     <li>{@link RuntimeException} - If the signup code is not found or expired.</li>
     *     <li>{@link RuntimeException} - If the lock box is not found.</li>
     *     <li>{@link RuntimeException} - If the lock box is already owned by another user.</li>
     * </ul>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * POST /boxes/signup
     * {
     *     "code": "123456",
     *     "macAddress": "00:1A:2B:3C:4D:5E"
     * }
     * }</pre>
     * This request registers the lock box with MAC address {@code 00:1A:2B:3C:4D:5E} using
     * the signup code {@code 123456}, associating it with the corresponding owner.
     *
     * @param request a {@link BoxSignupRequest} containing the signup code and MAC address of the lock box
     * @return a {@link Mono} containing a {@link ResponseEntity} with an {@link ApiResponse.Body}
     * wrapping the registered {@link BoxDto}
     * @throws RuntimeException if the signup code is not found, the lock box is not found,
     *                          or the lock box is already owned
     */
    @PostMapping("/signup")
    public Mono<ResponseEntity<ApiResponse.Body<BoxDto>>> signup(
            final @RequestBody BoxSignupRequest request
    ) {
        return Mono.zip(
                        boxSignupService.findByCode(request.getCode())
                                .switchIfEmpty(Mono.error(new RuntimeException("signup.code.not.found")))
                                .map(BoxSignup::getIssuerSub),
                        boxService.findByMacAddress(request.getMacAddress())
                                .switchIfEmpty(Mono.error(new RuntimeException("box.not.found")))
                ).<Mono<Box>>handle((tuple, sink) -> {
                    if (tuple.getT2().getOwnerSub() != null) {
                        sink.error(new RuntimeException("already.owned"));
                        return;
                    }

                    Box updatedBox = tuple.getT2().setOwnerSub(tuple.getT1());
                    sink.next(boxService.save(updatedBox));
                }).flatMap(box -> box.map(boxMapper::toDto)
                        .map(ApiResponse::ok))
                .onErrorResume(e -> Mono.just(ApiResponse.bad(e.getMessage())));
    }

    /**
     * Updates the status of an existing lock box.
     * <p>
     * This endpoint allows clients to update the status of a lock box by providing its MAC address
     * and the new status details. The status may include information such as battery level, signal strength,
     * and whether the lock box is open. The method ensures that the specified lock box exists before applying
     * the updates.
     * </p>
     *
     * <p><strong>Request Body:</strong></p>
     * The request body should contain a {@link BoxStatusDto} with the following fields:
     * <ul>
     *     <li>{@code macAddress} - The MAC address of the lock box whose status is being updated.
     *     Must be in the valid MAC address format.</li>
     *     <li>{@code batteryLevel} - Optional. The battery level of the lock box as a percentage.</li>
     *     <li>{@code signalStrength} - Optional. The signal strength of the lock box.</li>
     *     <li>{@code opened} - Optional. A boolean indicating whether the lock box is open.</li>
     * </ul>
     *
     * <p><strong>Returns:</strong></p>
     * <ul>
     *     <li>A {@link BoxStatusDto} containing the updated status details of the lock box.</li>
     * </ul>
     *
     * <p><strong>Throws:</strong></p>
     * <ul>
     *     <li>{@link RuntimeException} - If the specified lock box does not exist.</li>
     * </ul>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * PATCH /boxes/status
     * {
     *     "macAddress": "00:1A:2B:3C:4D:5E",
     *     "batteryLevel": 85.0,
     *     "signalStrength": -45,
     *     "opened": true
     * }
     * }</pre>
     * This request updates the lock box with MAC address {@code 00:1A:2B:3C:4D:5E}, setting its battery level
     * to {@code 85.0%}, signal strength to {@code -45 dBm}, and marking it as open.
     *
     * @param request a {@link BoxStatusDto} containing the new status details of the lock box
     * @return a {@link Mono} containing a {@link ResponseEntity} with an {@link ApiResponse.Body}
     * wrapping the updated {@link BoxStatusDto}
     * @throws RuntimeException if the specified lock box does not exist
     */
    @PatchMapping("/status")
    public Mono<ResponseEntity<ApiResponse.Body<BoxStatusDto>>> status(
            final @RequestBody BoxStatusDto request
    ) {
        return boxService.findByMacAddress(request.getMacAddress())
                .switchIfEmpty(Mono.error(new RuntimeException("box.not.found")))
                .flatMap(_box -> boxStatusService.save(boxStatusMapper.toEntity(request)))
                .map(boxStatusMapper::toDto)
                .map(ApiResponse::ok);
    }

    /**
     * Retrieves updates for a specific lock box based on its MAC address and a timestamp.
     * <p>
     * This endpoint allows clients to fetch all updates associated with a lock box identified by
     * its MAC address. Updates are filtered by a timestamp to include only those that occurred
     * after the specified time. Pagination is supported using the {@code page} and {@code offset}
     * parameters to control the number of records returned.
     * </p>
     *
     * <p><strong>Path Parameters:</strong></p>
     * <ul>
     *     <li>{@code macAddress} - The MAC address of the lock box for which updates are requested.</li>
     * </ul>
     *
     * <p><strong>Query Parameters:</strong></p>
     * <ul>
     *     <li>{@code afterTimestamp} - A mandatory timestamp indicating the starting point for updates.
     *     Must be in the past.</li>
     *     <li>{@code page} - Optional. The page number of updates to retrieve. Default is {@code 1}.
     *     Must be greater than or equal to {@code 1}.</li>
     *     <li>{@code offset} - Optional. The number of updates per page. Default is {@code 0}.
     *     Must be greater than or equal to {@code 0}.</li>
     * </ul>
     *
     * <p><strong>Returns:</strong></p>
     * <ul>
     *     <li>A {@link BoxUpdatesResponse} containing a list of updates for the lock box and
     *     the current server timestamp.</li>
     * </ul>
     *
     * <p><strong>Throws:</strong></p>
     * <ul>
     *     <li>{@link IllegalArgumentException} - If validation for {@code page}, {@code offset},
     *     or {@code afterTimestamp} fails.</li>
     * </ul>
     *
     * <p><strong>Usage Example:</strong></p>
     * <pre>{@code
     * GET /boxes/00:1A:2B:3C:4D:5E/updates?afterTimestamp=2025-01-01T12:00:00Z&page=2&offset=10
     * }</pre>
     * This request fetches updates for the lock box with MAC address {@code 00:1A:2B:3C:4D:5E},
     * starting from {@code 2025-01-01T12:00:00Z}, retrieving the second page with 10 updates per page.
     *
     * @param macAddress     the MAC address of the lock box
     * @param afterTimestamp the timestamp after which updates should be fetched; must not be null and must be in past
     * @param page           the page number of updates to retrieve; defaults to {@code 1};
     *                       must be greater than or equal to {@code 1}
     * @param offset         the number of updates per page; defaults to {@code 0};
     *                       must be greater than or equal to {@code 0}
     * @return a {@link Mono} containing a {@link ResponseEntity} with an {@link ApiResponse.Body}
     * wrapping a {@link BoxUpdatesResponse}
     */
    @GetMapping("/{macAddress}/updates")
    public Mono<ResponseEntity<ApiResponse.Body<BoxUpdatesResponse>>> getUpdates(
            @PathVariable String macAddress,
            @RequestParam @NotNull(message = "afterTimestamp.not_null") @Past(message = "afterTimestamp.past")
            Timestamp afterTimestamp,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "page.min:1") int page,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "offset.min:0") int offset
    ) {
        return boxUpdateService
                .findAllByMacAddressAndTimestampAfter(macAddress, afterTimestamp)
                .skip((long) (page - 1) * offset)
                .take(offset)
                .map(publicBoxUpdateMapper::toDto)
                .collectList()
                .map(updates -> new BoxUpdatesResponse(updates, Timestamp.from(Instant.now())))
                .map(ApiResponse::ok);
    }
}
