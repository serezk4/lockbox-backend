package com.lockbox.box.database.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.UUID;

/**
 * DTO for {@link com.lockbox.box.database.model.BoxUpdate}
 */
@Value
public class BoxUpdateDto {

    /**
     * Unique identifier for the update record.
     * <p>Must not be null.</p>
     */
    @NotNull(message = "id.not_null")
    Long id;

    /**
     * MAC address of the box associated with this update.
     * <p>Must not be null and must follow the format XX:XX:XX:XX:XX:XX where X is a hexadecimal digit.</p>
     */
    @NotNull(message = "macAddress.not_null")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;

    /**
     * Action or call performed on the box.
     * <p>Must not be null and maximum length of 50 characters.</p>
     * <p>Example values: "LOCK", "UNLOCK", "UPDATE_CONFIG".</p>
     */
    @NotNull(message = "call.not_null")
    @Size(max = 50, message = "call.max_length:50")
    String call;

    /**
     * Identifier of the user who initiated the update.
     * <p>Optional. Maximum length of 255 characters.</p>
     */
    @Size(max = 255, message = "issuerSub.max_length:255")
    String issuerSub;

    /**
     * Unique token of the issuer.
     * <p>Optional.</p>
     */
    UUID issuerToken;
}
