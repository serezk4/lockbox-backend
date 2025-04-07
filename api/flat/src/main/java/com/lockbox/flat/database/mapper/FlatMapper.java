package com.lockbox.flat.database.mapper;

import com.lockbox.flat.database.dto.FlatDto;
import com.lockbox.flat.database.model.Flat;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FlatMapper {
    Flat toEntity(FlatDto flatDto);

    FlatDto toDto(Flat flat);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Flat partialUpdate(FlatDto flatDto, @MappingTarget Flat flat);
}
