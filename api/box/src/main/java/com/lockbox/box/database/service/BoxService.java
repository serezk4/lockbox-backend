package com.lockbox.box.database.service;

import com.lockbox.box.database.model.Box;
import com.lockbox.box.database.repository.BoxRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for managing {@link Box} entities. Provides methods for saving boxes
 * and retrieving them by owner ID.
 * <p>
 * This class handles business logic related to box management and ensures transactions
 * operate with {@code READ_COMMITTED} isolation to maintain data integrity.
 * </p>
 * <p>
 * The {@code BoxService} leverages reactive types such as {@link Mono} and {@link Flux}
 * for non-blocking operations, optimizing resource usage in reactive applications.
 * </p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Service} - Indicates that this class is a service component.</li>
 *     <li>{@link Transactional} - Configures transaction management with rollback for exceptions.</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.0
 * @see Box
 * @see BoxRepository
 * @since 1.0
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxService {

    /**
     * Repository for performing CRUD operations on {@link Box} entities.
     */
    BoxRepository boxRepository;

    DatabaseClient databaseClient;

    /**
     * Saves a {@link Box} entity to the database.
     *
     * @param box the {@link Box} entity to save
     * @return a {@link Mono} containing the saved {@link Box} entity
     */
    public Mono<Box> save(Box box) {
        return boxRepository.save(box);
    }

    /**
     * Updates or inserts a {@link Box} entity in the database.
     *
     * @param box the {@link Box} entity to upsert
     * @return a {@link Mono} containing the upserted {@link Box} entity
     */
    public Mono<Box> upsert(Box box) {
        return boxRepository.upsert(box);
    }

    /**
     * Retrieves a {@link Box} entity by its MAC address.
     *
     * @param macAddress the MAC address of the box
     * @return a {@link Mono} containing the {@link Box} entity with the specified MAC address
     */
    public Mono<Box> findByMacAddress(String macAddress) {
        return boxRepository.findById(macAddress);
    }

    /**
     * Retrieves the total count of {@link Box} entities associated with a specific owner ID.
     *
     * @param ownerSub the ID of the owner
     * @return a {@link Mono} containing the total count of {@link Box} entities matching the owner ID
     */
    public Mono<Long> countAllByOwnerSub(String ownerSub) {
        return boxRepository.countAllByOwnerSub(ownerSub);
    }

    /**
     * Checks if a {@link Box} entity exists with the specified MAC address and owner ID.
     *
     * @param macAddress the MAC address of the box
     * @param ownerSub   the ID of the owner
     * @return a {@link Mono} containing a box mac address
     */
    public Mono<String> existsByMacAddressAndOwnerSub(String macAddress, String ownerSub) {
        return boxRepository.existsByMacAddressAndOwnerSub(macAddress, ownerSub)
                .filter(exists -> exists).map(exists -> macAddress);
    }

    /**
     * Retrieves a {@link Box} entity by its MAC address and owner ID.
     *
     * @param macAddress the MAC address of the box
     * @param ownerSub   the ID of the owner
     * @return a {@link Mono} containing the {@link Box} entity with the specified MAC address and owner ID
     */
    public Mono<Box> findByMacAddressAndOwnerSub(String macAddress, String ownerSub) {
        return boxRepository.findByMacAddressAndOwnerSub(macAddress, ownerSub);
    }
}
