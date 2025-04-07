package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.CreateBoxAccessDto;
import com.lockbox.box.database.model.BoxAccess;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreateBoxAccessMapper {
    BoxAccess toEntity(CreateBoxAccessDto createBoxAccessDto);

    CreateBoxAccessDto toDto(BoxAccess boxAccess);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BoxAccess partialUpdate(CreateBoxAccessDto createBoxAccessDto, @MappingTarget BoxAccess boxAccess);
}
