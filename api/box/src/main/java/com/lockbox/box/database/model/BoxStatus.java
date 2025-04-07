package com.lockbox.box.database.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

/**
 * Represents the status of a {@link Box} at a given point in time. This entity corresponds to the "box_statuses" table
 * and provides details such as battery level, signal strength, and timestamp for each status record.
 * <p>
 * The {@code BoxStatus} class is useful for tracking the real-time status and health metrics of a box. Each record
 * includes a timestamp to enable chronological tracking.
 * </p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Table} - Maps the class to the "box_statuses" table.</li>
 *     <li>{@link FieldDefaults} - Sets fields to private access level.</li>
 *     <li>{@link AllArgsConstructor} - Generates a constructor with parameters for all fields.</li>
 *     <li>{@link RequiredArgsConstructor} - Generates a constructor for required fields.</li>
 *     <li>{@link Getter} and {@link Setter} - Generates getters and setters for all fields.</li>
 *     <li>{@link ToString} - Generates a toString method.</li>
 *     <li>{@link Builder} - Provides a builder pattern for object creation.</li>
 *     <li>{@link Accessors} - Allows for chaining in setters.</li>
 * </ul>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * BoxStatus status = BoxStatus.builder()
 *                             .macAddress("00:1A:2B:3C:4D:5E")
 *                             .batteryLevel(75.0)
 *                             .signalStrength(-50.0)
 *                             .opened(false)
 *                             .timestamp(new Timestamp(System.currentTimeMillis()))
 *                             .build();
 * }</pre>
 *
 * @author serezk4
 * @version 1.1
 * @see Box
 * @since 1.0
 */
@Table(name = "box_statuses")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class BoxStatus {

    /**
     * Unique identifier for the status record.
     */
    @Id
    @NotNull(message = "id.not_null")
    Long id;

    /**
     * MAC address of the box associated with this status.
     */
    @Column("mac_address")
    @NotNull(message = "mac.address.not_null")
    String macAddress;

    /**
     * Battery level of the box at the time of this status.
     * <p>Value must be between 0 and 100.</p>
     */
    @Column("battery_level")
    @NotNull(message = "battery.level.not_null")
    @Min(value = 0, message = "battery.level.min:0")
    @Max(value = 100, message = "battery.level.max:100")
    Double batteryLevel;

    /**
     * Signal strength of the box at the time of this status.
     * <p>Value must be between -100 and 0 dBm.</p>
     */
    @Column("signal_strength")
    @NotNull(message = "signal.strength.not_null")
    @Min(value = -100, message = "signal.strength.min:-100")
    @Max(value = 0, message = "signal.strength.max:0")
    Double signalStrength;

    /**
     * Boolean indicating whether the box is currently opened.
     */
    @NotNull(message = "opened.not_null")
    Boolean opened;

    /**
     * Timestamp indicating when this status was recorded.
     */
    @Column("timestamp")
    @NotNull(message = "timestamp.not_null")
    Timestamp timestamp;
}
