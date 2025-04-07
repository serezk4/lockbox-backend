package com.lockbox.flat.database.dto;

import com.lockbox.flat.database.model.Flat;
import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.lockbox.flat.database.model.Flat}
 * <p>
 * Represents a data transfer object for the {@code Flat} entity, ensuring
 * all necessary validation constraints are applied during data exchange
 * between layers.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Value
public class FlatDto implements Serializable {

    /**
     * Unique identifier for the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    UUID flatId;

    /**
     * Identifier of the owner associated with this flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @NotNull(message = "owner_id.not_null")
    UUID ownerId;

    /**
     * Title of the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be blank</li>
     *     <li>Must be between 4 and 255 characters</li>
     * </ul>
     */
    @NotBlank(message = "title.not_blank")
    @Size(min = 4, max = 255, message = "title.size:4-255")
    String title;

    /**
     * Detailed description of the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be blank</li>
     *     <li>Must be at least 11 characters</li>
     * </ul>
     */
    @NotBlank(message = "description.not_blank")
    @Size(min = 11, message = "description.min:11")
    String description;

    /**
     * Longitude of the flat's location.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be between -180.0 and 180.0</li>
     * </ul>
     */
    @DecimalMin(value = "-180.0", message = "longitude.min:-180.0")
    @DecimalMax(value = "180.0", message = "longitude.max:180.0")
    Double longitude;

    /**
     * Latitude of the flat's location.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be between -90.0 and 90.0</li>
     * </ul>
     */
    @DecimalMin(value = "-90.0", message = "latitude.min:-90.0")
    @DecimalMax(value = "90.0", message = "latitude.max:90.0")
    Double latitude;

    /**
     * Floor number of the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a non-negative integer</li>
     * </ul>
     */
    @Min(value = 0, message = "floor.min:0")
    Integer floor;

    /**
     * Total area of the flat in square meters.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     *     <li>Must be greater than 0.01</li>
     *     <li>Max precision: 4, scale: 2</li>
     * </ul>
     */
    @NotNull(message = "area.not_null")
    @DecimalMin(value = "0.01", inclusive = true, message = "area.min:0.01")
    @Digits(integer = 2, fraction = 2, message = "area.digits:4,2")
    Double area;

    /**
     * Number of rooms in the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     *     <li>Must be at least 1</li>
     * </ul>
     */
    @NotNull(message = "rooms.not_null")
    @Min(value = 1, message = "rooms.min:1")
    Integer rooms;

    /**
     * Status of the flat (e.g., available, hidden, unavailable).
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be blank</li>
     * </ul>
     */
    @NotBlank(message = "status.not_blank")
    Flat.Status status;

    /**
     * Timestamp when the flat was created.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @NotNull(message = "created_at.not_null")
    Instant createdAt;

    /**
     * Timestamp when the flat was last updated.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @NotNull(message = "updated_at.not_null")
    Instant updatedAt;
}
