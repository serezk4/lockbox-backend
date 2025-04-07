package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.BoxStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for {@link BoxStatus} entities, providing reactive CRUD operations.
 * <p>
 * This interface extends {@link ReactiveCrudRepository}, enabling reactive support for non-blocking
 * database operations, which is particularly beneficial in high-throughput applications.
 * </p>
 *
 * <p>Custom query methods, such as {@link #findFirstByMacAddressOrderByTimestampDesc(String)}, allow efficient
 * retrieval of the latest {@link BoxStatus} for a specified MAC address.</p>
 *
 * @author serezk4
 * @version 1.0
 * @see BoxStatus
 * @see ReactiveCrudRepository
 * @since 1.0
 */
@Repository
public interface BoxStatusRepository extends ReactiveCrudRepository<BoxStatus, Long> {

    /**
     * Retrieves the latest {@link BoxStatus} for a given MAC address.
     *
     * @param macAddress the MAC address of the box
     * @return a {@link Mono} containing the latest {@link BoxStatus} for the specified MAC address
     */
    Mono<BoxStatus> findFirstByMacAddressOrderByTimestampDesc(String macAddress);
}
