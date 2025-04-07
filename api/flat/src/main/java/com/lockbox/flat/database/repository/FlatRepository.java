package com.lockbox.flat.database.repository;

import com.lockbox.flat.database.model.Flat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Repository interface for managing {@link Flat} entities.
 * <p>
 * This interface extends {@link ReactiveCrudRepository} to provide
 * basic CRUD functionality and includes custom queries for advanced
 * filtering and pagination.
 * </p>
 * <p>
 * Queries are optimized for reactive data flow and leverage R2DBC to
 * ensure non-blocking operations. Additional methods allow for paginated
 * retrieval of flats based on geospatial and ownership filters.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface FlatRepository extends ReactiveCrudRepository<Flat, UUID>, CustomFlatRepository {

    /**
     * Retrieves all flats within a specified radius from a given latitude and longitude.
     * <p>
     * The query uses PostgreSQL's {@code earth_distance} function to calculate
     * the distance between two geographic points. Results are paginated using
     * {@code OFFSET} and {@code LIMIT}.
     * </p>
     *
     * @param latitude  the latitude of the central point
     * @param longitude the longitude of the central point
     * @param radius    the search radius in meters
     * @param offset    the number of rows to skip
     * @param limit     the maximum number of rows to return
     * @return a {@link Flux} containing the paginated list of {@link Flat} entities
     */
    @Query("""
             SELECT * 
             FROM flats
             WHERE earth_distance(
                 ll_to_earth(:latitude, :longitude),
                 ll_to_earth(latitude, longitude)
             ) <= :radius
             OFFSET :offset LIMIT :limit
            """)
    Flux<Flat> findAllByRadiusPageable(
            final @Param("latitude") double latitude,
            final @Param("longitude") double longitude,
            final @Param("radius") double radius,
            final @Param("offset") int offset,
            final @Param("limit") int limit
    );

    /**
     * Retrieves all flats associated with a specific owner, with pagination support.
     * <p>
     * The query filters flats by the {@code owner_id} and applies pagination
     * using {@code OFFSET} and {@code LIMIT}.
     * </p>
     *
     * @param ownerId the unique identifier of the owner
     * @param offset  the number of rows to skip
     * @param limit   the maximum number of rows to return
     * @return a {@link Flux} containing the paginated list of {@link Flat} entities
     */
    @Query("""
             SELECT * 
             FROM flats
             WHERE owner_id = :ownerId
             OFFSET :offset LIMIT :limit
            """)
    Flux<Flat> findAllByOwnerIdPageable(
            final @Param("ownerId") UUID ownerId,
            final @Param("offset") int offset,
            final @Param("limit") int limit
    );
}
