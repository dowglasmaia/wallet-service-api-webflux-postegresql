package com.dowglasmaia.wallet.repository;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface TransactionRepository extends R2dbcRepository<TransactionEntity, String> {


    /*@Query("SELECT * FROM transactions WHERE user_id = :userId AND date_time BETWEEN :initialDate AND :finalDate ORDER BY date_time DESC")
    Flux<TransactionEntity> findStatementByUserId(String userId, LocalDateTime startDate, LocalDateTime endDate);*/

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND date_time BETWEEN :initialDate AND :finalDate ORDER BY date_time DESC")
    Flux<TransactionEntity> findStatementByUserId(String userId, LocalDateTime initialDate, LocalDateTime finalDate);

    Mono<TransactionEntity> findFirstByOrderByDateTimeDesc(String userId);
}
