package com.lockbox.flat.database.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an amenity that can be associated with a flat.
 * <p>
 * Examples include WiFi, pet-friendly, child-friendly, etc.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Table(name = "amenities")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class Amenity {

    /**
     * Unique identifier for the amenity.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    @Id
    @Column("amenity_id")
    UUID amenityId;

    /**
     * Name of the amenity.
     * <p>
     * Examples: WiFi, Pet Friendly, Child Friendly.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be blank</li>
     *     <li>Must be unique</li>
     *     <li>Minimum length: 3 characters</li>
     * </ul>
     */
    @NotBlank(message = "amenity.name.not_blank")
    @Size(min = 3, max = 100, message = "amenity.name.size:3-100")
    @Column("name")
    String name;

    /**
     * Timestamp when the amenity was created.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @NotNull(message = "created_at.not_null")
    @Column("created_at")
    Instant createdAt = Instant.now();

    /**
     * Timestamp when the amenity was last updated.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @NotNull(message = "updated_at.not_null")
    @Column("updated_at")
    Instant updatedAt = Instant.now();
}
