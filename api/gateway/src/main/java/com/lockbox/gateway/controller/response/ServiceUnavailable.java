package com.lockbox.gateway.controller.response;

import lombok.Value;

@Value
public class ServiceUnavailable {
    String serviceName;
    String status = "Service is currently unavailable.";
}
