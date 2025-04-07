package com.lockbox.box.database.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * Represents a storage box entity in the database.
 * This entity corresponds to the "boxes" table and provides mapping to the table's columns.
 * Each {@code Box} instance stores information about a box identified by its unique MAC address.
 * <p>
 * This class uses Lombok annotations for boilerplate code reduction and supports fluent-style accessors.
 * It includes an {@link #ownerSub} field to link the box to its owner, along with an {@link #alias}
 * for an optional user-defined name.
 * </p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Table} - Maps the class to the "boxes" table.</li>
 *     <li>{@link FieldDefaults} - Sets fields to private access level.</li>
 *     <li>{@link AllArgsConstructor} - Generates a constructor with parameters for all fields.</li>
 *     <li>{@link RequiredArgsConstructor} - Generates a constructor for required fields
 *     (those marked as final or with {@code @NonNull}).</li>
 *     <li>{@link Getter} and {@link Setter} - Generates getters and setters for all fields.</li>
 *     <li>{@link ToString} - Generates a toString method.</li>
 *     <li>{@link Builder} - Provides a builder pattern for object creation.</li>
 *     <li>{@link Accessors} - Allows for chaining in setters.</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * Box box = Box.builder()
 *              .macAddress("00:1A:2B:3C:4D:5E")
 *              .ownerId(12345L)
 *              .alias("My Storage Box")
 *              .build();
 * }</pre>
 *
 * <p><b>Note:</b> The {@code Box} class is immutable unless changed via setters or the builder.</p>
 *
 * @author serezk4
 * @version 1.1
 * @since 1.0
 */
@Table(name = "view_boxes_statuses")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class BoxWithStatus {
    /**
     * The MAC address uniquely identifying the box.
     * <p>Serves as the primary key in the "boxes" table.</p>
     */
    @Column("mac_address")
    @NotBlank(message = "mac.address.not_blank")
    @Pattern(
            regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$",
            message = "mac.address.invalid_format"
    )
    @Id
    String macAddress;

    /**
     * Identifier for the owner of the box.
     * <p>References an owner user sub, linking this box to its associated owner.</p>
     */
    @Column("owner_sub")
    @NotBlank(message = "owner.sub.not_blank")
    String ownerSub;

    /**
     * Optional alias or name for the box provided by the user.
     * <p>This field allows for a descriptive name for easier identification.</p>
     */
    @Column("alias")
    @Size(max = 100, message = "alias.max_length:100")
    String alias;

    /**
     * Address of the box.
     * <p>This field allows for a descriptive address for easier identification.</p>
     */
    @Column("address")
    @NotBlank(message = "address.not_blank")
    @Size(max = 255, message = "address.max_length:255")
    String address;

    /**
     * Flag from {@link BoxStatus}
     * <p>Indicates whether the box is locked or unlocked.</p>
     */
    @Column("opened")
    boolean opened;

    /**
     * Flag from {@link BoxStatus}
     * <p>Indicates battery level</p>
     */
    @Column("battery_level")
    @Min(value = 0, message = "battery.level.min:-100")
    @Max(value = 100, message = "battery.level.max:0")
    double batteryLevel;

    /**
     * Flag from {@link BoxStatus}
     * <p>Indicates signal strength</p>
     */
    @Column("signal_strength")
    @Min(value = -100, message = "signal.strength.min:-100")
    @Max(value = 0, message = "signal.strength.max:0")
    double signalStrength;

    @Column("last_status_timestamp")
    @NotNull(message = "timestamp.not_null")
    Timestamp timestamp;
}
