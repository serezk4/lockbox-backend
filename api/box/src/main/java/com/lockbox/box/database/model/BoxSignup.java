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

/**
 * Represents a sign-up record for a box in the database.
 * Each record tracks the unique code, issuer, and creation time for a specific sign-up process.
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Table} - Maps the class to the "box_signup" table.</li>
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
 * BoxSignup signup = BoxSignup.builder()
 *                             .id(1L)
 *                             .code(123456)
 *                             .issuerSub("user-sub-123")
 *                             .createdAt(new Timestamp(System.currentTimeMillis()))
 *                             .build();
 * }</pre>
 *
 * @author serezk4
 * @version 1.1
 * @since 1.0
 */
@Table(name = "box_signup")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class BoxSignup {

    /**
     * Unique identifier for the sign-up record.
     */
    @Id
    @NotNull(message = "id.not_null")
    Long id;

    /**
     * Unique code for the sign-up process.
     */
    @Column("code")
    @NotNull(message = "code.not_null")
    @Size(min = 6, max = 6, message = "code.size:6")
    Integer code;

    /**
     * Identifier of the issuer who created the sign-up record.
     */
    @Column("issuer_sub")
    @NotNull(message = "issuer.sub.not_null")
    String issuerSub;

    /**
     * Timestamp when the sign-up record was created.
     */
    @Column("created_at")
    @NotNull(message = "created.at.not_null")
    Timestamp createdAt;

    /**
     * Constructor for creating a sign-up record with an issuer.
     *
     * @param issuerSub the identifier of the issuer
     */
    public BoxSignup(String issuerSub) {
        this.issuerSub = issuerSub;
    }
}
