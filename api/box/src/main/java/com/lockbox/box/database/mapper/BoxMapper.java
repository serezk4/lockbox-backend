package com.lockbox.box.database.mapper;

import com.lockbox.box.database.dto.BoxDto;
import com.lockbox.box.database.model.Box;
import org.mapstruct.*;

/**
 * Mapper interface for converting between {@link Box} entity and {@link BoxDto} data transfer object.
 * This interface uses MapStruct for automatic mapping, allowing for seamless conversions between
 * entity and DTO representations of a {@code Box}.
 * <p>
 * MapStruct annotations are used to specify mapping behaviors and strategies, ensuring that mappings
 * adhere to application requirements, such as ignoring unmapped targets and handling partial updates.
 * </p>
 *
 * <p>Configuration:</p>
 * <ul>
 *     <li>{@link Mapper} - Configures the mapper with the
 *          Spring component model and ignores unmapped target properties.</li>
 *     <li>{@link BeanMapping} - Used for customizing mapping behavior in the {@link #partialUpdate(BoxDto, Box)}
 *          method, with a strategy to ignore null values.</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * // Converting BoxDto to Box entity
 * Box boxEntity = boxMapper.toEntity(boxDto);
 *
 * // Converting Box entity to BoxDto
 * BoxDto boxDto = boxMapper.toDto(boxEntity);
 *
 * // Partially updating an existing Box entity from a BoxDto
 * boxMapper.partialUpdate(boxDto, boxEntity);
 * }</pre>
 *
 * <p><b>Note:</b> The interface is intended to be implemented by MapStruct at build time,
 * so there is no need for an explicit implementation.</p>
 *
 * @author serezk4
 * @version 1.0
 * @see Box
 * @see BoxDto
 * @see org.mapstruct.Mapper
 * @since 1.0
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BoxMapper {

    /**
     * Converts a {@link BoxDto} to a {@link Box} entity.
     *
     * @param boxDto the data transfer object representing a box
     * @return a {@link Box} entity containing the data from the {@link BoxDto}
     */
    Box toEntity(BoxDto boxDto);

    /**
     * Converts a {@link Box} entity to a {@link BoxDto}.
     *
     * @param box the entity representing a box
     * @return a {@link BoxDto} containing the data from the {@link Box} entity
     */
    BoxDto toDto(Box box);

    /**
     * Updates an existing {@link Box} entity with non-null values from a {@link BoxDto}.
     * <p>
     * This method performs a partial update, copying only non-null properties from the provided
     * {@link BoxDto} to the target {@link Box} entity. This is useful for cases where only specific
     * fields need to be updated without overwriting other fields with null values.
     * </p>
     *
     * @param boxDto the data transfer object with updated fields
     * @param box    the target entity to be updated
     * @return the updated {@link Box} entity with applied changes
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "macAddress", ignore = true)
    Box partialUpdate(BoxDto boxDto, @MappingTarget Box box);
}
