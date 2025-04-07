package com.lockbox.flat.database.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * Represents the relationship between a flat and an amenity.
 * <p>
 * This join table allows for a many-to-many relationship between flats
 * and amenities.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Table(name = "flat_amenities")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FlatAmenity {

    /**
     * Identifier of the flat associated with this amenity.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    @NotNull(message = "flat_id.not_null")
    @Column("flat_id")
    UUID flatId;

    /**
     * Identifier of the amenity associated with the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    @NotNull(message = "amenity_id.not_null")
    @Column("amenity_id")
    UUID amenityId;
}
