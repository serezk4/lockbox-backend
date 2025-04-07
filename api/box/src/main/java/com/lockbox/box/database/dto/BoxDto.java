package com.lockbox.box.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Value;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for the {@link com.lockbox.box.database.model.Box} entity.
 * <p>
 * This class is used to transfer limited information about a {@code Box} in a simplified form.
 * The {@code alias} field provides an optional user-defined name for the box.
 * </p>
 * <p>
 * An immutable class, {@code BoxDto} ensures data consistency and thread safety when used
 * in a distributed or concurrent environment.
 * </p>
 *
 * @version 1.1
 * @see com.lockbox.box.database.model.Box
 * @since 1.0
 */
@Value
public class BoxDto implements Serializable {

    /**
     * The MAC address uniquely identifying the box.
     * <p>Must follow the format XX:XX:XX:XX:XX:XX where X is a hexadecimal digit.</p>
     */
    @NotBlank(message = "macAddress.not_blank")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;

    /**
     * Optional alias for the box, used as a user-defined label.
     * <p>Maximum length of 100 characters.</p>
     */
    @Size(max = 100, message = "alias.max_length:100")
    String alias;

    /**
     * The address of the box.
     * <p>Must not be blank and maximum length of 255 characters.</p>
     */
    @NotBlank(message = "address.not_blank")
    @Size(max = 255, message = "address.max_length:255")
    String address;
}
