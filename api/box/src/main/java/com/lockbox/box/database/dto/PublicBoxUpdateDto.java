package com.lockbox.box.database.dto;

import lombok.Value;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link com.lockbox.box.database.model.BoxUpdate}
 */
@Value
public class PublicBoxUpdateDto implements Serializable {
    Long id;
    String call;
    Timestamp timestamp;
}
