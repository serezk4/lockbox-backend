package com.lockbox.box.controller.response.web.box;

import com.lockbox.box.database.dto.BoxStatusDto;
import lombok.Value;

@Value
public class BoxStatusResponse {
    BoxStatusDto boxStatus;
}
