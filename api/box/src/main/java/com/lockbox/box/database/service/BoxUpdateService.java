package com.lockbox.box.database.service;

import com.lockbox.box.database.model.BoxUpdate;
import com.lockbox.box.database.repository.BoxUpdateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxUpdateService {
    BoxUpdateRepository boxUpdateRepository;

    public Mono<BoxUpdate> save(BoxUpdate boxUpdate) {
        return boxUpdateRepository.save(boxUpdate);
    }

    public Flux<BoxUpdate> findAllByMacAddressAndTimestampAfter(String macAddress, Timestamp timestamp) {
        return boxUpdateRepository.findAllByMacAddressAndTimestampAfter(macAddress, timestamp);
    }
}
