package com.lockbox.box.database.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link com.lockbox.box.database.model.BoxStatus}
 */
@Value
public class BoxStatusDto implements Serializable {

    /**
     * MAC address of the box associated with this status.
     * <p>Must follow the format XX:XX:XX:XX:XX:XX where X is a hexadecimal digit.</p>
     */
    @NotNull(message = "macAddress.not_null")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;

    /**
     * Battery level of the box at the time of this status.
     * <p>Must be between 0 and 100 (inclusive).</p>
     */
    @NotNull(message = "batteryLevel.not_null")
    @Min(value = 0, message = "batteryLevel.min:0")
    @Max(value = 100, message = "batteryLevel.max:100")
    Double batteryLevel;

    /**
     * Signal strength of the box at the time of this status.
     * <p>Must be between -100 and 0 dBm (inclusive).</p>
     */
    @NotNull(message = "signalStrength.not_null")
    @Min(value = -100, message = "signalStrength.min:-100")
    @Max(value = 0, message = "signalStrength.max:0")
    Double signalStrength;

    /**
     * Indicates whether the box is currently opened.
     * <p>Must not be null.</p>
     */
    @NotNull(message = "opened.not_null")
    Boolean opened;

    /**
     * Timestamp indicating when this status was recorded.
     * <p>Must not be null.</p>
     */
    @NotNull(message = "timestamp.not_null")
    Timestamp timestamp;
}
