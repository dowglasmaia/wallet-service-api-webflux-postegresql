package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import com.dowglasmaia.wallet.strategy.TransactionContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Log4j2
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionContext transactionContext;

    @Autowired
    public TransactionServiceImpl(TransactionRepository repository, TransactionContext transactionContext){
        this.repository = repository;
        this.transactionContext = transactionContext;
    }

    @Transactional
    @Override
    public Mono<TransactionEntity> create(TransactionEntity transaction){
        return fetchCurrentBalance(transaction.getUserId())
              .flatMap(currentBalance -> {
                  BigDecimal newBalance = transactionContext.executeStrategy(
                        transaction.getOperationType(),
                        currentBalance,
                        transaction.getAmount());
                  transaction.setBalance(newBalance);

                  return repository.save(transaction)
                        .doOnSuccess(savedTransaction -> log.info("Successfully created Transaction with ID: {}", savedTransaction.getId()))
                        .doOnError(error -> log.error("Failed to save Transaction: {}", error.getMessage()))
                        .onErrorResume(error -> Mono.error(new BusinessException("Failed to create Transaction", HttpStatus.UNPROCESSABLE_ENTITY)));
              })
              .doFirst(() -> log.info("Starting create Transaction"));
    }

    @Override
    public Mono<TransactionEntity> getByUserId(String userId){
        return repository.findByUserId(userId);
    }

    private Mono<BigDecimal> fetchCurrentBalance(String userId){
        return repository.findFirstByOrderByDateTimeDesc(userId)
              .map(TransactionEntity::getBalance)
              .switchIfEmpty(Mono.just(BigDecimal.ZERO));
    }
}
