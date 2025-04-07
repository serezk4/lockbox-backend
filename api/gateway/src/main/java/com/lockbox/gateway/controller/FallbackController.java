package com.lockbox.gateway.controller;

import com.lockbox.gateway.controller.response.ServiceUnavailable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/health")
    public ResponseEntity<String> healthFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service health check failed.");
    }

    @GetMapping("/fallback/{serviceName}")
    public ResponseEntity<ServiceUnavailable> fallback(
            @PathVariable("serviceName") final String serviceName
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ServiceUnavailable(serviceName));
    }
}
