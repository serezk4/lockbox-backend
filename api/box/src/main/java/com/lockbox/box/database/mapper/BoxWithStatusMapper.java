package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.BoxWithStatusDto;
import com.lockbox.box.database.model.BoxWithStatus;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoxWithStatusMapper {
    BoxWithStatus toEntity(BoxWithStatusDto boxWithStatusDto);

    BoxWithStatusDto toDto(BoxWithStatus boxWithStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxWithStatus partialUpdate(BoxWithStatusDto boxWithStatusDto, @MappingTarget BoxWithStatus boxWithStatus);
}
