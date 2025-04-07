package com.lockbox.box.database.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for creating a new {@link com.lockbox.box.database.model.BoxAccess}
 */
@Value
public class CreateBoxAccessDto implements Serializable {

    /**
     * MAC address of the box being accessed.
     * <p>Must follow the format XX:XX:XX:XX:XX:XX where X is a hexadecimal digit.</p>
     */
    @NotNull(message = "macAddress.not_null")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;

    /**
     * Start time of the access.
     * <p>Must not be null.</p>
     */
    @NotNull(message = "startTime.not_null")
    Timestamp startTime;

    /**
     * End time of the access.
     * <p>Must not be null and should be after the start time.</p>
     */
    @NotNull(message = "endTime.not_null")
    Timestamp endTime;
}
