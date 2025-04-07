package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.Box;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for {@link Box} entities, providing reactive CRUD operations.
 * <p>
 * This interface extends {@link ReactiveCrudRepository}, enabling reactive support for
 * non-blocking database operations, which is especially useful in high-throughput applications.
 * </p>
 *
 * <p>
 * Custom query methods, such as {@link #findAllByOwnerSub(String)}, allow for the retrieval of
 * boxes based on specific conditions. This approach ensures efficient data handling by filtering
 * data server-side before returning the results as a reactive {@link Flux}.
 * </p>
 *
 * @author serezk4
 * @version 1.0
 * @see Box
 * @see ReactiveCrudRepository
 * @since 1.0
 */
@Repository
public interface BoxRepository extends ReactiveCrudRepository<Box, String> {

    /**
     * Retrieves the total count of {@link Box} entities associated with a specific owner ID.
     *
     * @param ownerSub the sub of the owner whose boxes are to be counted
     * @return a {@link Mono} containing the total count of {@link Box} entities matching the owner ID
     */
    Mono<Long> countAllByOwnerSub(String ownerSub);

    /**
     * Retrieves a {@link Box} entity by its MAC address and owner ID.
     *
     * @param macAddress the MAC address of the box
     * @param ownerSub   the sub of the owner whose box is to be retrieved
     * @return a {@link Mono} containing the {@link Box} entity with the specified MAC address and owner ID
     */
    Mono<Box> findByMacAddressAndOwnerSub(String macAddress, String ownerSub);

    /**
     * Checks if a {@link Box} entity exists with the specified MAC address and owner ID.
     *
     * @param macAddress the MAC address of the box
     * @param ownerSub   the sub of the owner whose box is to be checked
     * @return a {@link Mono} containing a boolean value indicating if the box exists
     */
    Mono<Boolean> existsByMacAddressAndOwnerSub(String macAddress, String ownerSub);

    /**
     * Performs an upsert operation on the {@link Box} entity.
     *
     * @param box the {@link Box} entity to upsert
     */
    @Query("""
                INSERT INTO boxes (mac_address, owner_sub, alias, address)
                VALUES (:#{#box.macAddress}, :#{#box.ownerSub}, :#{#box.alias}, :#{#box.address})
                ON CONFLICT (mac_address) DO UPDATE
                SET owner_sub = :#{#box.ownerSub},
                    alias = :#{#box.alias},
                    address = :#{#box.address}
                RETURNING *
            """)
    Mono<Box> upsert(Box box);
}
