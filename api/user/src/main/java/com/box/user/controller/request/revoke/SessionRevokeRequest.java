package com.box.user.controller.request.revoke;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SessionRevokeRequest {

    @NotBlank(message = "session_id:must_not_be_blank")
    String sessionId;
}
