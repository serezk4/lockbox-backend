package com.lockbox.box.database.service;

import com.lockbox.box.database.model.BoxWithStatus;
import com.lockbox.box.database.repository.BoxWithStatusRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxWithStatusService {
    BoxWithStatusRepository boxWithStatusRepository;

    public Mono<BoxWithStatus> findByMacAddress(String macAddress) {
        return boxWithStatusRepository.findById(macAddress);
    }

    public Flux<BoxWithStatus> findAllByOwnerSub(String ownerSub, int page, int size) {
        return boxWithStatusRepository.findAllByOwnerSub(ownerSub, size, page * size);
    }

    public Mono<BoxWithStatus> findByOwnerSubAndMacAddress(String ownerSub, String macAddress) {
        return boxWithStatusRepository.findByOwnerSubAndMacAddress(ownerSub, macAddress);
    }
}
