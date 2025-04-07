package com.lockbox.box.database.service;

import com.lockbox.box.database.model.BoxAccess;
import com.lockbox.box.database.repository.BoxAccessRepository;
import com.lockbox.box.database.util.TokenGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxAccessService {
    BoxAccessRepository boxAccessRepository;

    public Mono<BoxAccess> save(BoxAccess boxAccess) {
        return boxAccessRepository.save(boxAccess.setToken(TokenGenerator.generateToken()));
    }

    public Mono<BoxAccess> findById(UUID id) {
        return boxAccessRepository.findById(id);
    }

    public Mono<BoxAccess> findByToken(String token) {
        return boxAccessRepository.findByToken(token);
    }

    public Mono<Void> deleteById(UUID id) {
        return boxAccessRepository.deleteById(id);
    }

    public Flux<BoxAccess> findAllByMacAddress(String macAddress) {
        return boxAccessRepository.findAllByMacAddress(macAddress);
    }
}
