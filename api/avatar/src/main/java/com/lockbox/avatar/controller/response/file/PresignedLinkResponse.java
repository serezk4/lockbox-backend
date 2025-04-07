package com.lockbox.avatar.controller.response.file;

import lombok.Value;

import java.net.URL;

/**
 * Represents a response containing a presigned URL for avatar upload or download operations.
 *
 * <p>Usage examples:</p>
 * <ul>
 *     <li>Uploading an avatar: Clients receive a presigned URL and use it to upload a file via HTTP PUT.</li>
 *     <li>Downloading an avatar: Clients receive a presigned URL to fetch the stored avatar via HTTP GET.</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Value
public class PresignedLinkResponse {

    /**
     * The presigned URL that allows the client to perform a secure operation
     * (either uploading or downloading an avatar).
     *
     * <p>This URL is time-sensitive and will expire after a predefined duration.</p>
     */
    URL link;
}
