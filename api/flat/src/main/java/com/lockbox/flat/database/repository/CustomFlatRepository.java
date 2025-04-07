package com.lockbox.flat.database.repository;

import com.lockbox.flat.database.dto.FlatFilterDto;
import com.lockbox.flat.database.model.FlatWithLatestPrice;
import reactor.core.publisher.Flux;

public interface CustomFlatRepository {
    Flux<FlatWithLatestPrice> findFlatsByFilters(FlatFilterDto filter, int offset, int limit);
}
