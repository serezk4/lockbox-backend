package com.lockbox.flat.database.repository;

import com.lockbox.flat.database.dto.FlatFilterDto;
import com.lockbox.flat.database.model.Flat;
import com.lockbox.flat.database.model.FlatWithLatestPrice;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementation of the {@link CustomFlatRepository} interface, providing advanced filtering capabilities
 * for {@link Flat} entities. Combines the power of Spring Data R2DBC Criteria API with custom SQL for
 * complex queries such as geolocation-based filtering.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomFlatRepositoryImpl implements CustomFlatRepository {

    // todo а оно работает вообще? ://
    R2dbcEntityTemplate r2dbcEntityTemplate;
    DatabaseClient databaseClient;

    /**
     * Retrieves a list of flats filtered by the given criteria.
     * Supports dynamic filtering by amenities, area, rooms, status, and geolocation radius.
     *
     * @param filter the filtering criteria
     * @param offset the starting point for pagination
     * @param limit  the maximum number of results to return
     * @return a {@link Flux} of {@link Flat} objects matching the filters
     */
    @Override
    public Flux<FlatWithLatestPrice> findFlatsByFilters(
            final FlatFilterDto filter,
            final int offset, final int limit
    ) {
        Criteria criteria = Criteria.empty();

        if (filter.getAmenities() != null && !filter.getAmenities().isEmpty())
            criteria = criteria.and("flat_id").in(filter.getAmenities());
        if (filter.getMinArea() != null)
            criteria = criteria.and("area").greaterThanOrEquals(filter.getMinArea());
        if (filter.getMaxArea() != null)
            criteria = criteria.and("area").lessThanOrEquals(filter.getMaxArea());
        if (filter.getMinRooms() != null)
            criteria = criteria.and("rooms").greaterThanOrEquals(filter.getMinRooms());
        if (filter.getMaxRooms() != null)
            criteria = criteria.and("rooms").lessThanOrEquals(filter.getMaxRooms());
        if (filter.getStatus() != null && !filter.getStatus().isEmpty())
            criteria = criteria.and("status").is(filter.getStatus());
        if (filter.getMinPrice() != null)
            criteria = criteria.and("latest_price").greaterThanOrEquals(filter.getMinPrice());
        if (filter.getMaxPrice() != null)
            criteria = criteria.and("latest_price").lessThanOrEquals(filter.getMaxPrice());


        Query query = Query.query(criteria)
                .offset(offset)
                .limit(limit)
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        if (filter.getLatitude() == null || filter.getLongitude() == null || filter.getRadius() == null) {
            // todo add search here
            return r2dbcEntityTemplate.select(FlatWithLatestPrice.class)
                    .matching(query)
                    .all();
        }

        String sql = String.join(" ", """
                            SELECT * FROM v_flats_with_latest_price 
                            WHERE flat_id = ANY(:ids) 
                            AND earth_distance(ll_to_earth(:lat, :lon), ll_to_earth(latitude, longitude)) <= :radius 
                        """,
                (filter.getSearch() != null ? "AND (title ~* :search OR description ~* :search)" : ""),
                ("OFFSET :off LIMIT :lim"));

        if (filter.getOrderBy() != null && filter.getOrderDirection() != null) {
            query = query.sort(Sort.by(Sort.Direction.fromString(filter.getOrderDirection()), filter.getOrderBy()));
        }

        return r2dbcEntityTemplate.select(FlatWithLatestPrice.class)
                .matching(query).all()
                .map(FlatWithLatestPrice::getFlatId)
                .collectList()
                .flatMapMany(flatIds -> {
                    if (flatIds.isEmpty()) return Flux.empty();

                    DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql)
                            .bind("ids", flatIds.toArray(new UUID[0]))
                            .bind("lat", filter.getLatitude())
                            .bind("lon", filter.getLongitude())
                            .bind("radius", filter.getRadius())
                            .bind("off", offset)
                            .bind("lim", limit);

                    return spec.map(this::mapRowToFlat).all();
                });
    }

    /**
     * Maps a database row to a {@link Flat} object.
     *
     * @param row the database row
     * @return a {@link Flat} object populated with data from the row
     */
    private FlatWithLatestPrice mapRowToFlat(Row row, RowMetadata metadata) {
        return FlatWithLatestPrice.builder()
                .flatId(row.get("flat_id", UUID.class))
                .ownerSub(row.get("owner_sub", String.class))
                .title(row.get("title", String.class))
                .description(row.get("description", String.class))
                .longitude(row.get("longitude", Double.class))
                .latitude(row.get("latitude", Double.class))
                .floor(row.get("floor", Integer.class))
                .area(row.get("area", Double.class))
                .rooms(row.get("rooms", Integer.class))
                .status(Flat.Status.valueOf(row.get("status", String.class)))
                .createdAt(row.get("created_at", Instant.class))
                .updatedAt(row.get("updated_at", Instant.class))
                .latestPrice(row.get("latest_price", Double.class))
                .build();
    }
}
