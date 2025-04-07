package com.lockbox.flat.database.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a read-only model for retrieving Flats along with their latest prices
 * from the <b>v_flats_with_latest_price</b> database view.
 * <p>
 * This entity maps to a view that consolidates data from both the
 * <i>flats</i> and <i>flat_prices</i> tables, thereby providing the latest
 * recorded price for each flat without the need for manual subqueries.
 * <p>
 * Note: Write operations (inserts/updates) are typically unsupported for entities
 * representing database views.
 */
@Table("v_flats_with_latest_price")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Accessors(chain = true)
public class FlatWithLatestPrice {

    /**
     * Unique identifier of the flat.
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
     * Identifier of the owner associated with this flat (optional in the view).
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>owner_id.not_null</li>
     *     <li>Must be a valid UUID</li>
     * </ul>
     */
    @Column("owner_sub")
    String ownerSub;

    /**
     * Title of the flat.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>title.not_blank</li>
     *     <li>title.size:4-255</li>
     * </ul>
     */
    @Column("title")
    String title;

    /**
     * Detailed description of the flat.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>description.not_blank</li>
     *     <li>description.min:11</li>
     * </ul>
     */
    @Column("description")
    String description;

    /**
     * Longitude of the flat's location.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>longitude.min:-180.0</li>
     *     <li>longitude.max:180.0</li>
     * </ul>
     */
    @Column("longitude")
    @DecimalMin(value = "-180.0", message = "longitude.min:-180.0")
    @DecimalMax(value = "180.0", message = "longitude.max:180.0")
    Double longitude;

    /**
     * Latitude of the flat's location.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>latitude.min:-90.0</li>
     *     <li>latitude.max:90.0</li>
     * </ul>
     */
    @Column("latitude")
    @DecimalMin(value = "-90.0", message = "latitude.min:-90.0")
    @DecimalMax(value = "90.0", message = "latitude.max:90.0")
    Double latitude;

    /**
     * Floor number of the flat.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>floor.min:0</li>
     * </ul>
     */
    @Column("floor")
    Integer floor;

    /**
     * Total area of the flat (in square meters).
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>area.not_null</li>
     *     <li>area.min:0.01</li>
     *     <li>area.digits:4,2</li>
     * </ul>
     */
    @Column("area")
    Double area;

    /**
     * Number of rooms in the flat.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>rooms.not_null</li>
     *     <li>rooms.min:1</li>
     * </ul>
     */
    @Column("rooms")
    Integer rooms;

    /**
     * Current status of the flat (for example, available, hidden, or unavailable).
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>status.not_blank</li>
     * </ul>
     */
    @Column("status")
    Flat.Status status;

    /**
     * Timestamp indicating when the flat was created.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>created_at.not_null</li>
     * </ul>
     */
    @Column("created_at")
    Instant createdAt;

    /**
     * Timestamp indicating when the flat was last updated.
     * <p>
     * <b>Restrictions (underlying table):</b>
     * <ul>
     *     <li>updated_at.not_null</li>
     * </ul>
     */
    @Column("updated_at")
    Instant updatedAt;

    /**
     * The latest recorded price for the flat.
     * <p>
     * <b>Note:</b>
     * <ul>
     *     <li>Derived from the <i>flat_prices</i> table.</li>
     * </ul>
     */
    @Column("latest_price")
    Double latestPrice;
}
