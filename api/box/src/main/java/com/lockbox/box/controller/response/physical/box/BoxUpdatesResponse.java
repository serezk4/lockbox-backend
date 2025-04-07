package com.lockbox.box.controller.response.physical.box;

import com.lockbox.box.database.dto.PublicBoxUpdateDto;
import lombok.Value;

import java.sql.Timestamp;
import java.util.List;

@Value
public class BoxUpdatesResponse {
    List<PublicBoxUpdateDto> updates;
    Timestamp lastUpdate;
}
