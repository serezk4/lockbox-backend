package com.lockbox.box.controller.request.web.access;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.sql.Timestamp;

// todo add custom validator for time range (end time must be after start time) & (start time must be in the future)

@Value
public class ShareAccessRequest {
    Timestamp startTime;

    @NotNull(message = "end.time.null")
    Timestamp endTime;
}
