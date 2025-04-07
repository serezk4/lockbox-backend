package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.Box;
import com.lockbox.box.database.model.BoxWithStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BoxWithStatusRepository extends ReactiveCrudRepository<BoxWithStatus, String> {

    /**
     * Retrieves all {@link Box} entities associated with a specific owner ID.
     *
     * @param ownerSub the sub of the owner whose boxes are to be retrieved
     * @param limit    the maximum number of boxes to retrieve
     * @param offset   the number of boxes to skip before starting to return results
     * @return a {@link Flux} containing all {@link Box} entities matching the owner ID
     */
    @Query("""
                SELECT * FROM view_boxes_statuses
                WHERE owner_sub = :ownerSub
                ORDER BY mac_address
                LIMIT :limit OFFSET :offset
            """)
    Flux<BoxWithStatus> findAllByOwnerSub(String ownerSub, int limit, int offset);

    Mono<BoxWithStatus> findByOwnerSubAndMacAddress(String ownerSub, String macAddress);
}
