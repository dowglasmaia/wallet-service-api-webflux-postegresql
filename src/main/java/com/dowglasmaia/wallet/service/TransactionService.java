package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransactionService {

    Mono<TransactionEntity> create(TransactionEntity transaction);

    Flux<TransactionEntity> getStatementgetByUserId(String userId, LocalDateTime startDate, LocalDateTime endDate);

    Mono<TransactionEntity> getBalanceByUserId(String userId);

}
