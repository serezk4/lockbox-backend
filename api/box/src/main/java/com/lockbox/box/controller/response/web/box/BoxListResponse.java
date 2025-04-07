package com.lockbox.box.controller.response.web.box;

import com.lockbox.box.database.dto.BoxWithStatusDto;
import lombok.Value;

import java.util.List;

@Value
public class BoxListResponse {
    List<BoxWithStatusDto> boxes;
    long totalBoxes;
    int currentPage;
    int boxesOnPage;
}
