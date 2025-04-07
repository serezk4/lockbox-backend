package com.lockbox.box.database.repository;

import com.lockbox.box.database.model.BoxSignup;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BoxSignupRepository extends ReactiveCrudRepository<BoxSignup, Long> {
    Mono<BoxSignup> findByCode(Integer code);
}
