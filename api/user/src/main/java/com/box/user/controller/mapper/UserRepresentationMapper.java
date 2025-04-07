package com.box.user.controller.mapper;

import com.box.user.controller.dto.UserRepresentationDto;
import com.box.user.controller.request.user.UserEditRequest;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserRepresentationMapper {

    UserRepresentationDto toDto(UserRepresentation userRepresentation);

    UserRepresentation toEntity(UserRepresentationDto userRepresentationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserRepresentation partialUpdate(UserEditRequest editRequest, @MappingTarget UserRepresentation userRepresentation);
}
