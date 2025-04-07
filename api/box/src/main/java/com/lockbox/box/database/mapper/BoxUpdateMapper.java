package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.BoxUpdateDto;
import com.lockbox.box.database.model.BoxUpdate;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoxUpdateMapper {
    BoxUpdate toEntity(BoxUpdateDto boxUpdateDto);

    BoxUpdateDto toDto(BoxUpdate boxUpdate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxUpdate partialUpdate(BoxUpdateDto boxUpdateDto, @MappingTarget BoxUpdate boxUpdate);
}
