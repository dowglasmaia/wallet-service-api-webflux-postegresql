package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends ReactiveCrudRepository<TransactionEntity, Long> {

    Mono<TransactionEntity> findByUserId(String userId);
}
