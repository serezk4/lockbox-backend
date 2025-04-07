package com.box.user.controller.request.introspect;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IntrospectTokenRequest(
        @NotNull(message = "accessToken.null")
        @NotBlank(message = "accessToken.blank")
        String accessToken
) {
}
