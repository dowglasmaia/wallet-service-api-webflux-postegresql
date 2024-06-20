package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, Long> {

    Mono<TransactionEntity> findByUserId(String userId);

    Mono<TransactionEntity> findFirstByOrderByDateTimeDesc(String userId);
}
