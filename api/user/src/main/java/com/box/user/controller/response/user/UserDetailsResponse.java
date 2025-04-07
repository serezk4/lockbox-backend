package com.box.user.controller.response.user;

import com.box.user.security.auth.model.CustomUserDetails;
import lombok.Value;

@Value
public class UserDetailsResponse {
    CustomUserDetails userDetails;
}
