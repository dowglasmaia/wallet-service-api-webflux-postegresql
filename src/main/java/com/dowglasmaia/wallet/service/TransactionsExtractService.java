package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface TransactionsExtractService {

    Flux<TransactionEntity> findStatementByUserId(String userId, LocalDateTime startDate, LocalDateTime endDate);

}
