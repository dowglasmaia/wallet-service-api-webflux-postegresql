package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.RefundEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.enums.TransactionTypeEnum;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.repository.AccountRepository;
import com.dowglasmaia.wallet.repository.RefundRepository;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import com.dowglasmaia.wallet.service.AuditService;
import com.dowglasmaia.wallet.service.CreditAdjustmentTransactionService;
import com.dowglasmaia.wallet.strategy.TransactionContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Service
public class CreditAdjustmentTransactionServiceImpl implements CreditAdjustmentTransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;
    private final TransactionContext transactionContext;
    private final AuditService auditService;

    @Autowired
    public CreditAdjustmentTransactionServiceImpl(
          AccountRepository accountRepository,
          TransactionContext transactionContext,
          AuditService auditService,
          RefundRepository refundRepository,
          TransactionRepository transactionRepository
    ){
        this.accountRepository = accountRepository;
        this.transactionContext = transactionContext;
        this.auditService = auditService;
        this.refundRepository = refundRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public Mono<Void> processCancellationAndRefund(String userId, String transactionId){
        return findTransactionByIdAndUser(userId, transactionId)
              .flatMap(transaction ->
                    accountRepository.findByUserId(userId)
                          .switchIfEmpty(Mono.error(new BusinessException("Account not found", HttpStatus.NOT_FOUND)))
                          .flatMap(account -> handleRefund(transaction, account))
              )
              .doFirst(() -> log.info("Starting processCancellationAndRefund"))
              .doOnError(error -> log.error("Error processing cancellation and refund", error))
              .then();
    }

    private Mono<TransactionEntity> findTransactionByIdAndUser(String userId, String transactionId){
        return transactionRepository.findByIdAndUserId(UUID.fromString(transactionId), userId)
              .switchIfEmpty(Mono.error(new BusinessException("Transaction not found", HttpStatus.NOT_FOUND)))
              .flatMap(this::validatePurchaseTransaction)
              .doFirst(() -> log.info("Starting findTransactionByIdAndUser"))
              .doOnSubscribe(subscription -> log.info("Validating Refund Transaction"));
    }

    private Mono<TransactionEntity> validatePurchaseTransaction(TransactionEntity transaction){
        log.info("Starting validatePurchaseTransaction");
        if (!"PURCHASE".equals(transaction.getOperationType())) {
            log.error("Invalid operation for this operationType: {}", transaction.getOperationType());
            return Mono.error(new BusinessException(
                  String.format("Invalid operation for this operationType: %s", transaction.getOperationType()),
                  HttpStatus.UNPROCESSABLE_ENTITY
            ));
        }
        return Mono.just(transaction);
    }

    private Mono<Void> handleRefund(TransactionEntity transaction, AccountEntity account){
        return refundRepository.existsByTransactionIdAndUserId(transaction.getId(), transaction.getUserId())
              .flatMap(exists -> {
                  if (exists) {
                      String errorMessage = "Operation not performed; it has already been processed for this transaction";
                      log.error(errorMessage);
                      return Mono.error(new BusinessException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY));
                  } else {
                      return performRefund(transaction, account);
                  }
              })
              .doFirst(() -> log.info("Starting handleRefund"));
    }

    private Mono<Void> performRefund(TransactionEntity transaction, AccountEntity account){
        return Mono.defer(() -> {
            TransactionEntity refundTransaction = createRefundTransaction(transaction);
            BigDecimal newBalance = transactionContext.executeStrategy(
                  refundTransaction.getOperationType(),
                  account.getBalance(),
                  refundTransaction.getAmount()
            );

            account.setBalance(newBalance);

            return Mono.when(
                  transactionRepository.save(refundTransaction),
                  accountRepository.save(account),
                  refundRepository.save(createRefundEntity(transaction))
            ).doOnSuccess(unused -> auditService.sendMessage(refundTransaction));
        });
    }

    private TransactionEntity createRefundTransaction(TransactionEntity transaction){
        log.info("Creating refund transaction");
        return TransactionEntity.builder()
              .userId(transaction.getUserId())
              .operationType(TransactionTypeEnum.REFUND.name())
              .amount(transaction.getAmount())
              .dateTime(LocalDateTime.now())
              .build();
    }

    private RefundEntity createRefundEntity(TransactionEntity refundTransaction){
        log.info("Creating refund entity");
        return RefundEntity.builder()
              .transactionId(refundTransaction.getId())
              .userId(refundTransaction.getUserId())
              .amount(refundTransaction.getAmount())
              .dateTime(LocalDateTime.now())
              .build();
    }

}
