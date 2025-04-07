package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.BoxAccess;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BoxAccessRepository extends ReactiveCrudRepository<BoxAccess, UUID> {
    Mono<BoxAccess> findByToken(String token);

    Flux<BoxAccess> findAllByMacAddress(String macAddress);
}
