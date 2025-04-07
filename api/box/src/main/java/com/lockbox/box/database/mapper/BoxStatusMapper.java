package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.BoxStatusDto;
import com.lockbox.box.database.model.BoxStatus;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoxStatusMapper {
    BoxStatus toEntity(BoxStatusDto boxStatusDto);

    BoxStatusDto toDto(BoxStatus boxStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxStatus partialUpdate(BoxStatusDto boxStatusDto, @MappingTarget BoxStatus boxStatus);
}
