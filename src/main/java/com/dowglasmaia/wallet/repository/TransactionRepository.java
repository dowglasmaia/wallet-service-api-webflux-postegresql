package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, String> {

    Flux<TransactionEntity> findByUserIdAndDateTimeBetween(String userId, LocalDateTime initialDate, LocalDateTime finalDate);

    Mono<TransactionEntity> findByIdAndUserId(UUID uuid, String userId);
}
