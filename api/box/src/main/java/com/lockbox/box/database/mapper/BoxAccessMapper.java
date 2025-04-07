package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.BoxAccessDto;
import com.lockbox.box.database.model.BoxAccess;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoxAccessMapper {
    BoxAccess toEntity(BoxAccessDto boxAccessDto);

    BoxAccessDto toDto(BoxAccess boxAccess);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxAccess partialUpdate(BoxAccessDto boxAccessDto, @MappingTarget BoxAccess boxAccess);
}
