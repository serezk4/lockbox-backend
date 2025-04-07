package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.BoxUpdate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;

@Repository
public interface BoxUpdateRepository extends ReactiveCrudRepository<BoxUpdate, Long> {
    Flux<BoxUpdate> findAllByMacAddressAndTimestampAfter(String macAddress, Timestamp timestamp);
}
