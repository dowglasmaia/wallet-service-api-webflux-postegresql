package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.RefundEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefundRepository extends R2dbcRepository<RefundEntity, String> {

    @Query("SELECT EXISTS(SELECT 1 FROM refund WHERE transaction_id = :transactionId AND user_id = :userId)")
    Mono<Boolean> existsByTransactionIdAndUserId(UUID transactionId, String userId);

}
