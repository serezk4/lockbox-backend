package com.lockbox.flat.database.service;

import com.lockbox.flat.database.dto.FlatFilterDto;
import com.lockbox.flat.database.model.Flat;
import com.lockbox.flat.database.model.FlatWithLatestPrice;
import com.lockbox.flat.database.repository.FlatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service class responsible for managing {@link Flat} entities.
 * Provides methods for saving, retrieving, and filtering flats with transactional support.
 * Includes comprehensive logging for better traceability.
 *
 * @author serezk4
 * @version 1.0
 * @since 1.0
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class FlatService {

    /**
     * Repository for accessing {@link Flat} data in the database.
     */
    FlatRepository flatRepository;

    /**
     * Saves a new or existing flat to the database.
     *
     * @param flat the {@link Flat} entity to save
     * @return a {@link Mono} emitting the saved {@link Flat} entity
     */
    public Mono<Flat> save(final Flat flat) {
        log.info("Saving flat: {}", flat);
        return flatRepository.save(flat)
                .doOnSuccess(savedFlat -> log.info("Flat saved successfully: {}", savedFlat))
                .doOnError(error -> log.error("Failed to save flat: {}", flat, error));
    }

    /**
     * Retrieves all flats within a specified radius from a given location.
     *
     * @param latitude  the latitude of the center point
     * @param longitude the longitude of the center point
     * @param radius    the search radius in meters
     * @param page      the page index for pagination (zero-based)
     * @param size      the number of results per page
     * @return a {@link Flux} emitting the {@link Flat} entities matching the criteria
     */
    public Flux<Flat> findAllByRadius(
            final double latitude,
            final double longitude,
            final double radius,
            final int page,
            final int size
    ) {
        log.info("Finding flats by radius: latitude={}, longitude={}, radius={}, page={}, size={}",
                latitude, longitude, radius, page, size);
        return flatRepository.findAllByRadiusPageable(
                        latitude, longitude, radius,
                        page * size, size
                ).doOnComplete(() -> log.info("Completed finding flats by radius"))
                .doOnError(error -> log.error("Error finding flats by radius", error));
    }

    /**
     * Retrieves flats based on dynamic filtering criteria.
     *
     * @param filter the filtering criteria as a {@link FlatFilterDto}
     * @param page   the page index for pagination (zero-based)
     * @param size   the number of results per page
     * @return a {@link Flux} emitting the {@link Flat} entities matching the filters
     */
    public Flux<FlatWithLatestPrice> findAllByFilters(
            final FlatFilterDto filter,
            final int page,
            final int size
    ) {
        log.info("Finding flats by filters: filter={}, page={}, size={}", filter, page, size);
        return flatRepository.findFlatsByFilters(filter, page * size, size)
                .doOnComplete(() -> log.info("Completed finding flats by filters"))
                .doOnError(error -> log.error("Error finding flats by filters", error));
    }

    /**
     * Retrieves a flat by its unique identifier.
     *
     * @param id the unique identifier of the flat
     * @return a {@link Mono} emitting the {@link Flat} entity, or {@code Mono.empty()} if not found
     */
    public Mono<Flat> findById(final UUID id) {
        log.info("Finding flat by ID: {}", id);
        return flatRepository.findById(id)
                .doOnSuccess(flat -> {
                    if (flat != null) log.info("Flat found: {}", flat);
                    else log.warn("No flat found for ID: {}", id);
                })
                .doOnError(error -> log.error("Error finding flat by ID: {}", id, error));
    }

    /**
     * Retrieves all flats owned by a specific owner, with pagination support.
     *
     * @param ownerId the unique identifier of the owner
     * @param page    the page index for pagination (zero-based)
     * @param size    the number of results per page
     * @return a {@link Flux} emitting the {@link Flat} entities owned by the specified owner
     */
    public Flux<Flat> findAllByOwnerId(
            final UUID ownerId,
            final int page,
            final int size
    ) {
        log.info("Finding flats by owner ID: {}, page={}, size={}", ownerId, page, size);
        return flatRepository.findAllByOwnerIdPageable(ownerId, page * size, size)
                .doOnComplete(() -> log.info("Completed finding flats by owner ID: {}", ownerId))
                .doOnError(error -> log.error("Error finding flats by owner ID: {}", ownerId, error));
    }
}
