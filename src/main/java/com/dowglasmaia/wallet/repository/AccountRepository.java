package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.AccountEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends R2dbcRepository<AccountEntity, String> {

    Mono<AccountEntity> findByUserId(String userId);
}
