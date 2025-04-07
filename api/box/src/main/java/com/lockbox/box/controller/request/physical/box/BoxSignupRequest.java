package com.lockbox.box.controller.request.physical.box;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
public class BoxSignupRequest {
    @NotNull(message = "code.required")
    Integer code;

    @NotNull(message = "mac.address.not_null")
    @NotBlank(message = "macAddress.not_blank")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "macAddress.invalid_format"
    )
    String macAddress;
}
