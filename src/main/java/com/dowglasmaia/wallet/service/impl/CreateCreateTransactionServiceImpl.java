package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.repository.AccountRepository;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import com.dowglasmaia.wallet.service.AuditService;
import com.dowglasmaia.wallet.service.CreateTransactionService;
import com.dowglasmaia.wallet.service.mapper.TransactionMapper;
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
public class CreateCreateTransactionServiceImpl implements CreateTransactionService {

    private final AccountRepository repository;
    private final TransactionRepository transactionRepository;
    private final TransactionContext transactionContext;
    private final AuditService auditService;

    @Autowired
    public CreateCreateTransactionServiceImpl(
          AccountRepository repository,
          TransactionContext transactionContext,
          AuditService auditService,
          TransactionRepository transactionRepository
    ){
        this.repository = repository;
        this.transactionContext = transactionContext;
        this.auditService = auditService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public Mono<AccountEntity> create(TransactionEntity transactionEntity){
        return repository.findByUserId(transactionEntity.getUserId())
              .switchIfEmpty(Mono.error(new BusinessException("Account not found for user ID: " + transactionEntity.getUserId(), HttpStatus.NOT_FOUND)))
              .flatMap(account -> {

                  BigDecimal newBalance = transactionContext.executeStrategy(
                        transactionEntity.getOperationType(),
                        account.getBalance(), transactionEntity.getAmount()
                  );

                  account.setBalance(newBalance);

                  return saveTransaction(account, transactionEntity.getOperationType(), transactionEntity.getAmount())
                        .then(repository.save(account));
              })
              .doFirst(() -> log.info("Starting create Transaction"))
              .doOnError(error -> log.error("Error during transaction creation", error));
    }

    private Mono<Void> saveTransaction(AccountEntity account, String operationType, BigDecimal amount){
        TransactionEntity transactionEntity = TransactionMapper.toTransactionEntity(account, operationType, amount);

        return transactionRepository.save(transactionEntity)
              .doOnSuccess(savedTransaction -> {
                  log.info("Successfully created Transaction with ID: {}", savedTransaction.getId());
                  auditService.sendMessage(savedTransaction); // Envio assíncrono da mensagem de auditoria
              })
              .doOnError(error -> {
                  log.error("Failed to save Transaction: {}", error.getMessage());
                  throw new BusinessException("Failed to create Transaction", HttpStatus.UNPROCESSABLE_ENTITY);
              })
              .then();
    }
}
