package com.lockbox.box.controller.request.physical.box;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.sql.Timestamp;

@Value
public class BoxUpdatesRequest {
    @NotNull(message = "mac.address.not_null")
    @NotBlank(message = "macAddress.not_blank")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;

    @NotNull(message = "afterTimestamp.not_null")
    @Past(message = "afterTimestamp.future")
    Timestamp afterTimestamp;

    @Min(value = 1, message = "page.min:1")
    int page;

    @Min(value = 0, message = "offset.min:0")
    int offset;
}
