package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<TransactionEntity> create(TransactionEntity transaction);

    Mono<TransactionEntity> getByUserId(String userId);

}
