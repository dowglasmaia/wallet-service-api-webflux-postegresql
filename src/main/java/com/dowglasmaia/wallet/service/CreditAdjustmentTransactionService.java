package com.dowglasmaia.wallet.service;

import reactor.core.publisher.Mono;

public interface CreditAdjustmentTransactionService {

     Mono<Void> processCancellationAndRefund(String userId, String transactionId);
}
