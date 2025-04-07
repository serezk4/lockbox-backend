package com.lockbox.flat.database.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a flat entity in the database.
 * <p>
 * The {@code Flat} class contains comprehensive information about a real estate property,
 * such as its title, description, location (latitude and longitude), size, number of rooms,
 * and ownership details. It also maintains status information to track availability
 * and timestamps for creation and last updates.
 * </p>
 * <p>
 * This entity is mapped to the {@code flats} table in the database and incorporates
 * validation constraints to ensure data integrity, including restrictions on size,
 * numeric ranges, and mandatory fields.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Table(name = "flats")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class Flat {

    /**
     * Unique identifier for the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    @Id
    @Column("flat_id")
    UUID flatId;

    /**
     * Identifier of the owner associated with this flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @Column("owner_sub")
    @NotNull(message = "owner_sub.not_null")
    String ownerSub;

    /**
     * Title of the flat.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be blank</li>
     *     <li>Must be between 4 and 255 characters</li>
     * </ul>
     */
    @Column("title")
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
    @Column("description")
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
    @Column("longitude")
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
    @Column("latitude")
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
    @Column("floor")
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
    @Column("area")
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
    @Column("rooms")
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
//    @Column("status")
//    @NotBlank(message = "status.not_blank")
//    Flat.Status status;

    /**
     * Enum for the status of the flat.
     * <p>
     * The {@code Status} enum defines the possible states for the availability of a flat.
     * </p>
     */
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    @Getter
    public enum Status {
        /**
         * The flat is available for rent or purchase.
         * <p>Typically used for flats that are ready for rent or purchase.</p>
         */
        AVAILABLE("available"),

        /**
         * The flat is hidden from public view.
         * <p>Typically used for flats that are not yet ready for rent or purchase.</p>
         */
        HIDDEN("hidden"),

        /**
         * The flat is unavailable for rent or purchase.
         * <p>Typically used for flats that are already rented or sold.</p>
         */
        UNAVAILABLE("unavailable");

        /**
         * Alias for the status.
         * <p>Provides a more descriptive name for the status.</p>
         */
        String alias;
    }

    /**
     * Timestamp when the flat was created.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @Column("created_at")
    @NotNull(message = "created_at.not_null")
    @Builder.Default
    Instant createdAt = Instant.now();

    /**
     * Timestamp when the flat was last updated.
     * <p>
     * <b>Restrictions:</b>
     * <ul>
     *     <li>Cannot be null</li>
     * </ul>
     */
    @Column("updated_at")
    @NotNull(message = "updated_at.not_null")
    @Builder.Default
    Instant updatedAt = Instant.now();
}
