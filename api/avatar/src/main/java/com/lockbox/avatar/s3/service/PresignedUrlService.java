package com.lockbox.avatar.s3.service;

import com.lockbox.avatar.s3.api.ReactiveS3Service;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URL;

/**
 * Service for generating pre-signed URLs for avatar uploads and downloads.
 *
 * <p>
 * This service provides functionalities for generating pre-signed URLs
 * for avatar uploads and downloads using the {@link ReactiveS3Service}.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Log4j2
public class PresignedUrlService {

    /**
     * Service for generating pre-signed URLs in a reactive manner.
     */
    ReactiveS3Service s3Service;

    /**
     * Generates a pre-signed URL for avatar download.
     *
     * @param userSub The user's sub to generate the URL for.
     * @return A {@link Mono} emitting the generated {@link URL}.
     */
    public Mono<URL> getDownloadLink(final @NotNull String userSub) {
        log.info("Requesting avatar download link for user: {}", userSub);
        return s3Service.generateDownloadLink(userSub);
    }

    /**
     * Generates a pre-signed URL for avatar upload.
     *
     * @param ownerSub The owner's sub to generate the URL for.
     * @return A {@link Mono} emitting the generated {@link URL}.
     */
    public Mono<URL> getUploadLink(final @NotNull String ownerSub) {
        log.info("Requesting avatar upload link for owner: {}", ownerSub);
        return s3Service.generateUploadLink(ownerSub);
    }
}
