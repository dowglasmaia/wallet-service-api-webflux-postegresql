package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import com.dowglasmaia.wallet.service.AuditService;
import com.dowglasmaia.wallet.service.TransactionService;
import com.dowglasmaia.wallet.strategy.TransactionContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionContext transactionContext;
    private final AuditService auditService;

    @Autowired
    public TransactionServiceImpl(
          TransactionRepository repository,
          TransactionContext transactionContext,
          AuditService auditService
    ){
        this.repository = repository;
        this.transactionContext = transactionContext;
        this.auditService = auditService;
    }

    @Transactional
    @Override
    public Mono<TransactionEntity> create(TransactionEntity transaction){
        return fetchAndCalculateBalance(transaction.getUserId(), transaction.getOperationType(), transaction.getAmount())
              .flatMap(newBalance -> {
                  transaction.setBalance(newBalance);
                  return saveTransaction(transaction);
              })
              .doFirst(() -> log.info("Starting create Transaction"));
    }

    @Override
    public Flux<TransactionEntity> getStatementByUserId(String userId, LocalDateTime startDate, LocalDateTime endDate){
        return repository.findStatementByUserId(userId, startDate, endDate);
    }

    @Override
    public Mono<TransactionEntity> getBalanceByUserId(String userId){
        return repository.findFirstByOrderByDateTimeDesc(userId);
    }

    private Mono<TransactionEntity> saveTransaction(TransactionEntity transaction){
        return repository.save(transaction)
              .doOnSuccess(savedTransaction -> {
                  log.info("Successfully created Transaction with ID: {}", savedTransaction.getId());
                  auditService.sendMessage(savedTransaction); // Envio assíncrono da mensagem de auditoria
              })
              .doOnError(error -> {
                  log.error("Failed to save Transaction: {}", error.getMessage());
                  throw new BusinessException("Failed to create Transaction", HttpStatus.UNPROCESSABLE_ENTITY);
              });
    }

    private Mono<BigDecimal> fetchAndCalculateBalance(String userId, String operationType, BigDecimal amount){
        return fetchCurrentBalance(userId)
              .map(currentBalance -> transactionContext.executeStrategy(operationType, currentBalance, amount))
              .switchIfEmpty(Mono.just(BigDecimal.ZERO));
    }

    private Mono<BigDecimal> fetchCurrentBalance(String userId){
        return this.getBalanceByUserId(userId)
              .map(TransactionEntity::getBalance)
              .switchIfEmpty(Mono.just(BigDecimal.ZERO));
    }
}
