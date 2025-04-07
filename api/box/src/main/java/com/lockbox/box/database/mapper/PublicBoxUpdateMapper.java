package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.PublicBoxUpdateDto;
import com.lockbox.box.database.model.BoxUpdate;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicBoxUpdateMapper {
    BoxUpdate toEntity(PublicBoxUpdateDto publicBoxUpdateDto);

    PublicBoxUpdateDto toDto(BoxUpdate boxUpdate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxUpdate partialUpdate(PublicBoxUpdateDto publicBoxUpdateDto, @MappingTarget BoxUpdate boxUpdate);
}
