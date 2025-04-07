package com.box.user.controller.request.user;

import lombok.Value;

@Value
public class UserEditRequest {
    String username;
    String firstName;
    String lastName;
    String email;
    Boolean enabled;
}
