package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.AccountEntity;
import reactor.core.publisher.Mono;

public interface FindBalanceService {

    Mono<AccountEntity> findByUserId(String userId);
}
