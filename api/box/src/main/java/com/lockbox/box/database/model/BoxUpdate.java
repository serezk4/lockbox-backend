package com.lockbox.box.database.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents an update action for a {@link Box} in the database.
 * This entity corresponds to the "box_updates" table and records details such as the MAC address,
 * action call, timestamp, and issuer information.
 *
 * <p>The {@code BoxUpdate} class tracks modifications or actions performed on a box,
 * including the user who initiated the action and their associated token.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Table} - Maps the class to the "box_updates" table.</li>
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
 * BoxUpdate update = BoxUpdate.builder()
 *                             .id(1L)
 *                             .macAddress("00:1A:2B:3C:4D:5E")
 *                             .call("LOCK")
 *                             .timestamp(new Timestamp(System.currentTimeMillis()))
 *                             .issuerSub("user-sub-123")
 *                             .issuerToken(UUID.randomUUID())
 *                             .build();
 * }</pre>
 *
 * @author serezk4
 * @version 1.1
 * @see Box
 * @since 1.0
 */
@Table(name = "box_updates")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class BoxUpdate {

    /**
     * Unique identifier for the update record.
     */
    @Id
    @NotNull(message = "id.not_null")
    Long id;

    /**
     * MAC address of the box associated with this update.
     */
    @Column("mac_address")
    @NotNull(message = "mac.address.not_null")
    String macAddress;

    /**
     * Action or call performed on the box.
     * <p>Example values: "LOCK", "UNLOCK", "UPDATE_CONFIG".</p>
     */
    @Column("call")
    @NotNull(message = "call.not_null")
    @Size(max = 50, message = "call.max_length:50")
    String call;

    /**
     * Timestamp indicating when the update action occurred.
     */
    @Column("timestamp")
    @NotNull(message = "timestamp.not_null")
    Timestamp timestamp;

    /**
     * Identifier of the user who initiated the update.
     */
    @Column("issuer_sub")
    @Size(max = 255, message = "issuer.sub.max_length:255")
    String issuerSub;

    /**
     * Unique token of the issuer.
     */
    @Column("issuer_token")
    UUID issuerToken;
}
