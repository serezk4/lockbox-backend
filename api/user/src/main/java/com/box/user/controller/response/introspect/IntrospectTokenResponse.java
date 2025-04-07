package com.box.user.controller.response.introspect;

import lombok.Value;

@Value
public class IntrospectTokenResponse {
    boolean active;
    long exp;
    long iat;
    String sub;
    String aud;
    String scope;
}
