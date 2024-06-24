package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import reactor.core.publisher.Mono;

public interface CreateTransactionService {

    Mono<AccountEntity> create(TransactionEntity transaction);

}
