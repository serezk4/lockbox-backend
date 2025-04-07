package com.lockbox.box.controller.rest.web;

import com.lockbox.box.database.model.Box;
import com.lockbox.box.database.model.BoxStatus;
import com.lockbox.box.database.service.BoxService;
import com.lockbox.box.database.service.BoxStatusService;
import com.lockbox.box.security.auth.model.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * REST controller for generating demo lock boxes and their corresponding status entries.
 * <p>
 * This controller provides an endpoint to generate a specified number of lock boxes with random
 * attributes for demonstration or testing purposes. Each generated box is associated with the authenticated user.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/demo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class GenerateController {
    BoxService boxService;
    BoxStatusService boxStatusService;

    /**
     * Generates a set of demo lock boxes along with their initial status entries.
     * <p>
     * This endpoint creates 10 lock boxes with randomly generated MAC addresses and default aliases.
     * For each created box, an initial {@link BoxStatus} entry is also generated with random values for
     * battery level, open status, and signal strength.
     * </p>
     *
     * @param userDetails the authenticated user's details, used to associate the generated boxes
     * @return a reactive {@link Flux} stream of {@link BoxStatus} objects representing the status of each generated box
     */
    @PostMapping
    public Flux<BoxStatus> generate(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Flux.fromIterable(
                        IntStream.range(0, 10)
                                .mapToObj(i -> createRandomMacAddress())
                                .toList()
                )
                .doOnNext(uuid -> log.info("Generating box with UUID: {}", uuid))
                .flatMap(macAddress -> {
                    Box box = Box.builder()
                            .macAddress(macAddress)
                            .ownerSub(userDetails.getSub())
                            .address("DEMO ADDRESS")
                            .alias("DEMO")
                            .build();
                    return boxService.upsert(box);
                })
                .flatMap(box -> {
                    BoxStatus boxStatus = BoxStatus.builder()
                            .macAddress(box.getMacAddress())
                            .batteryLevel(Math.random() * 100)
                            .opened(Math.random() > 0.5)
                            .signalStrength(-Math.random() * 99 - 1)
                            .build();
                    return boxStatusService.save(boxStatus);
                });
    }

    /**
     * Generates a random Media Access Control (MAC) address in the standard hexadecimal format.
     * <p>
     * This method creates a MAC address by generating six random bytes, ensuring that the
     * locally administered and unicast bits are set appropriately. The resulting MAC address
     * is formatted as a string with colon separators (e.g., "A1:B2:C3:D4:E5:F6").
     * </p>
     *
     * @return a randomly generated MAC address as a {@link String}
     */
    private String createRandomMacAddress() {
        Random random = new Random();
        byte[] macBytes = new byte[6];
        random.nextBytes(macBytes);

        macBytes[0] = (byte) (macBytes[0] & (byte) 0xFE);
        macBytes[0] = (byte) (macBytes[0] | (byte) 0x02);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < macBytes.length; i++) {
            sb.append(String.format("%02X", macBytes[i]));
            if (i < macBytes.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
