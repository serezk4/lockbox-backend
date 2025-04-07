package com.lockbox.avatar.s3.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Service for interacting with Amazon S3, providing functionalities
 * for generating pre-signed URLs for avatar uploads and downloads.
 *
 * <p>
 * This service initializes an S3 client using credentials and bucket details
 * provided through the application configuration.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Component
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class S3Service {

    /**
     * S3 Presigner for generating pre-signed URLs.
     */
    S3Presigner presigner;

    /**
     * Name of the S3 bucket where files are stored.
     * This value is used when generating pre-signed URLs.
     */
    String bucketName;

    /**
     * Default expiration time for pre-signed URLs (in milliseconds).
     * This value is used when generating pre-signed URLs without an explicit expiration time.
     */
    long expDefault;

    /**
     * Constructs an instance of {@code S3Service} and initializes
     * an S3 client with the provided credentials and configuration.
     *
     * @param accessKey   AWS access key for authentication.
     * @param secretKey   AWS secret key for authentication.
     * @param bucketName  Name of the S3 bucket.
     * @param endpointUrl Endpoint URL of the S3-compatible storage service.
     * @param region      AWS region where the S3 bucket is hosted.
     * @param expDefault  Default expiration duration for pre-signed URLs (in milliseconds).
     * @throws NullPointerException If any provided parameter is null.
     */
    public S3Service(
            final @NotBlank @Value("${s3.access-key}") String accessKey,
            final @NotBlank @Value("${s3.secret-key}") String secretKey,
            final @NotBlank @Value("${s3.bucket}") String bucketName,
            final @NotBlank @Value("${s3.endpoint}") URI endpointUrl,
            final @NotBlank @Value("${s3.region}") String region,
            final @Positive @Value("${s3.expDefault}") long expDefault
    ) {
        this.bucketName = bucketName;
        this.expDefault = expDefault;

        this.presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .endpointOverride(endpointUrl)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        log.info("S3Service initialized with bucket: {}", bucketName);
        log.debug("S3 Configuration - Region: {}, Endpoint: {}, Expiration Default: {}ms",
                region, endpointUrl, expDefault);
    }

    /**
     * Generates a pre-signed URL for uploading a avatar to S3.
     *
     * @param objectKey The key (path) of the object in the S3 bucket.
     * @return A {@link URL} that allows uploading the object.
     */
    public URL generateUploadLink(final @NotBlank String objectKey) {
        return presigner.presignPutObject(builder -> builder
                .signatureDuration(Duration.ofMillis(expDefault))
                .putObjectRequest(request -> request.bucket(bucketName).key(objectKey))
        ).url();
    }

    /**
     * Generates a pre-signed URL for downloading a avatar from S3.
     *
     * @param objectKey The key (path) of the object in the S3 bucket.
     * @return A {@link URL} that allows downloading the object.
     */
    public URL generateDownloadLink(final @NotBlank String objectKey) {
        return presigner.presignGetObject(builder -> builder
                .signatureDuration(Duration.ofMillis(expDefault))
                .getObjectRequest(request -> request.bucket(bucketName).key(objectKey))
        ).url();
    }
}
