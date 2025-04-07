package com.lockbox.box.database.dto;

import lombok.Value;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link com.lockbox.box.database.model.BoxWithStatus}
 */
@Value
public class BoxWithStatusDto implements Serializable {
    String macAddress;
    String ownerSub;
    String alias;
    String address;
    boolean opened;
    double batteryLevel;
    double signalStrength;
    Timestamp timestamp;
}
