package com.lockbox.flat.controller.rest;

import com.lockbox.flat.database.dto.FlatDto;
import com.lockbox.flat.database.mapper.FlatMapper;
import com.lockbox.flat.database.model.Flat;
import com.lockbox.flat.database.service.FlatService;
import com.lockbox.flat.security.auth.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@Log4j2
public class TestFlatController {
    FlatService flatService;

    FlatMapper flatMapper;

    @PostMapping
    public Mono<Flat> createFlat(
            final @Valid @RequestBody FlatDto flat,
            final @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return flatService.save(flatMapper.toEntity(flat)
                .setOwnerSub(userDetails.getSub())
        );
    }

    @PatchMapping("/{id}")
    public Mono<FlatDto> updateFlat(
            final @PathVariable UUID id,
            final @Valid @RequestBody FlatDto patch,
            final @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return flatService.findById(id)
                .filter(f -> f.getOwnerSub().equals(userDetails.getSub()))
                .switchIfEmpty(Mono.error(new IllegalAccessException("flat not found or not owned by user")))
                .flatMap(f -> flatService.save(flatMapper.partialUpdate(patch, f))
                        .doOnSuccess(updatedFlat -> log.info("Flat updated successfully: {}", updatedFlat))
                        .doOnError(error -> log.error("Failed to update flat: {}", id, error))
                ).map(flatMapper::toDto);
    }

}
