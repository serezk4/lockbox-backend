package com.lockbox.avatar.s3.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URL;

/**
 * Reactive service for generating pre-signed URLs for S3 operations.
 *
 * <p>
 * Uses {@link S3Service} for synchronous S3 interactions and wraps them in {@link Mono}
 * for non-blocking execution.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Service
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Log4j2
public class ReactiveS3Service {

    /**
     * Synchronous {@link S3Service} used for generating pre-signed URLs.
     */
    S3Service s3Service;

    /**
     * Generates a pre-signed URL for avatar upload in a reactive manner.
     *
     * @param objectKey The key (path) of the object in the S3 bucket.
     * @return A {@link Mono} emitting the generated {@link URL}.
     */
    public Mono<URL> generateUploadLink(final @NotBlank String objectKey) {
        log.info("Generating upload link for object key: {}", objectKey);
        return Mono.fromCallable(() -> s3Service.generateUploadLink(objectKey))
                .doOnError(e -> log.error("Failed to generate upload link for object key: {}", objectKey, e))
                .doOnSuccess(url -> log.info("Generated upload link for object key: {}", objectKey))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Generates a pre-signed URL for avatar download in a reactive manner.
     *
     * @param objectKey The key (path) of the object in the S3 bucket.
     * @return A {@link Mono} emitting the generated {@link URL}.
     */
    public Mono<URL> generateDownloadLink(final @NotBlank String objectKey) {
        log.info("Generating download link for object key: {}", objectKey);
        return Mono.fromCallable(() -> s3Service.generateDownloadLink(objectKey))
                .doOnError(e -> log.error("Failed to generate download link for object key: {}", objectKey, e))
                .doOnSuccess(url -> log.info("Generated download link for object key: {}", objectKey))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
