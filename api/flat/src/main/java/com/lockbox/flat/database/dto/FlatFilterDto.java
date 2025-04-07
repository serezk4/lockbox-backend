package com.lockbox.flat.database.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class FlatFilterDto {
    Double minArea;
    Double maxArea;
    Integer minRooms;
    Integer maxRooms;
    List<UUID> amenities;
    String status;
    Double latitude;
    Double longitude;
    Double radius;

    String orderBy;
    String orderDirection;
    String search;

    Double minPrice; // currency = rub
    Double maxPrice; // currency = rub
}
