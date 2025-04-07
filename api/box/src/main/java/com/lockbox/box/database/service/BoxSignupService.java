package com.lockbox.box.database.service;

import com.lockbox.box.database.model.BoxSignup;
import com.lockbox.box.database.repository.BoxSignupRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
@Log4j2
public class BoxSignupService {
    BoxSignupRepository boxSignupRepository;

    public Mono<BoxSignup> save(BoxSignup boxSignup) {
        return boxSignupRepository.save(boxSignup);
    }

    public Mono<BoxSignup> findById(Long id) {
        return boxSignupRepository.findById(id);
    }

    public Mono<BoxSignup> findByCode(Integer code) {
        return boxSignupRepository.findByCode(code);
    }
}
