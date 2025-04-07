package com.lockbox.box.database.service;

import com.lockbox.box.database.model.BoxStatus;
import com.lockbox.box.database.repository.BoxStatusRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service layer for managing {@link BoxStatus} entities, handling business logic related to
 * box status updates and retrieval.
 * <p>
 * This class operates with {@code READ_COMMITTED} isolation level to maintain data consistency,
 * ensuring that only committed data is read during transactions.
 * </p>
 * <p>
 * Reactive types, such as {@link Mono}, are used to support non-blocking I/O and optimize
 * resource usage in reactive applications.
 * </p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Service} - Indicates that this class is a Spring service component.</li>
 *     <li>{@link Transactional} - Manages transactional behavior with a rollback on exceptions.</li>
 * </ul>
 *
 * @author serezk4
 * @version 1.0
 * @see BoxStatus
 * @see BoxStatusRepository
 * @since 1.0
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxStatusService {

    /**
     * Repository for performing CRUD operations on {@link BoxStatus} entities.
     */
    BoxStatusRepository boxStatusRepository;

    /**
     * Saves a {@link BoxStatus} entity to the database.
     *
     * @param boxStatus the {@link BoxStatus} entity to save
     * @return a {@link Mono} containing the saved {@link BoxStatus} entity
     */
    public Mono<BoxStatus> save(BoxStatus boxStatus) {
        return boxStatusRepository.save(boxStatus);
    }

    /**
     * Retrieves the latest {@link BoxStatus} associated with a specific MAC address.
     *
     * @param macAddress the MAC address of the box
     * @return a {@link Mono} containing the latest {@link BoxStatus} for the specified MAC address
     */
    public Mono<BoxStatus> findTopByMacAddress(String macAddress) {
        return boxStatusRepository.findFirstByMacAddressOrderByTimestampDesc(macAddress)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Box status not found for MAC address: " + macAddress))
                );
    }
}
