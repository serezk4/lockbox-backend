package com.lockbox.box.database.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents an access record for a box in the database.
 * Each record tracks the access token, start, and end times for a specific box identified by its MAC address.
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Table} - Maps the class to the "box_accesses" table.</li>
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
 * BoxAccess access = BoxAccess.builder()
 *                             .uuid(UUID.randomUUID())
 *                             .macAddress("00:1A:2B:3C:4D:5E")
 *                             .token("unique-token")
 *                             .startTime(Timestamp.from(Instant.now()))
 *                             .endTime(Timestamp.from(Instant.now().plusSeconds(3600)))
 *                             .build();
 * }</pre>
 *
 * @author serezk4
 * @version 1.1
 * @since 1.0
 */
@Table(name = "box_accesses")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class BoxAccess {

    /**
     * Unique identifier for the access record.
     */
    @Id
    @NotNull(message = "uuid.not_null")
    UUID uuid;

    /**
     * MAC address of the box being accessed.
     */
    @Column("mac_address")
    @NotNull(message = "mac.address.not_null")
    String macAddress;

    /**
     * Unique token used for accessing the box.
     */
    @Column("token")
    @NotNull(message = "token.not_null")
    String token;

    /**
     * Start time of the access.
     * <p>Must not be null and defaults to the current timestamp if not set.</p>
     */
    @Column("start_time")
    @Builder.Default
    @NotNull(message = "start.time.not_null")
    Timestamp startTime = Timestamp.from(Instant.now());

    /**
     * End time of the access.
     * <p>Must not be null and should be in the future relative to the current timestamp.</p>
     */
    @Column("end_time")
    @NotNull(message = "end.time.not_null")
    @Future(message = "end.time.future")
    Timestamp endTime;
}
