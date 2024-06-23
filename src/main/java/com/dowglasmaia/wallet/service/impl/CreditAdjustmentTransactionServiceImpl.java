package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.RefundEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.enums.TransactionTypeEnum;
import com.dowglasmaia.wallet.exeptions.BusinessException;
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

    private final TransactionRepository transactionRepository;
    private final RefundRepository refundRepository;
    private final TransactionContext transactionContext;
    private final AuditService auditService;

    @Autowired
    public CreditAdjustmentTransactionServiceImpl(
          TransactionRepository transactionRepository,
          TransactionContext transactionContext,
          AuditService auditService,
          RefundRepository refundRepository
    ){
        this.transactionRepository = transactionRepository;
        this.transactionContext = transactionContext;
        this.auditService = auditService;
        this.refundRepository = refundRepository;
    }

    @Transactional
    public Mono<Void> processCancellationAndRefund(String userId, String transactionId){
        return findAndValidateTransaction(userId, transactionId)
              .doFirst(() -> log.info("Starting processCancellationAndRefund"))
              .flatMap(this::processTransaction)
              .flatMap(this::saveTransactionAndRefund)
              .doOnError(error -> log.error("Error processing cancellation and refund", error))
              .then();
    }

    private Mono<TransactionEntity> findAndValidateTransaction(String userId, String transactionId){
        return transactionRepository.findByIdAndUserId(UUID.fromString(transactionId), userId)
              .doFirst(() -> log.info("Starting findAndValidateTransaction"))
              .switchIfEmpty(Mono.error(new BusinessException("Transaction not found", HttpStatus.NOT_FOUND)))
              .flatMap(transaction -> validateOperationTypePurchase(transaction.getOperationType())
                    .thenReturn(transaction))
              .doOnSubscribe(subscription -> log.info("Starting Refund Transaction"));
    }

    private Mono<TransactionEntity> processTransaction(TransactionEntity transaction){
        log.info("Starting processTransaction");
        transaction.setOperationType(TransactionTypeEnum.REFUND.name());
        BigDecimal newBalance = transactionContext.executeStrategy(
              transaction.getOperationType(),
              transaction.getBalance(),
              transaction.getAmount());
        transaction.setBalance(newBalance);
        return Mono.just(transaction);
    }

    private Mono<Void> saveTransactionAndRefund(TransactionEntity transaction){
        return refundRepository.existsByTransactionIdAndUserId(transaction.getId(), transaction.getUserId())
              .doFirst(() -> log.info("Starting saveTransactionAndRefund"))
              .flatMap(exists -> {
                  if (exists) {
                      String errorMessage = "Operation not performed; it has already been processed for this transaction";
                      log.error(errorMessage);
                      return Mono.error(new BusinessException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY));
                  } else {
                      return transactionRepository.save(getTransactionEntity(transaction))
                            .flatMap(savedTransaction -> refundRepository.save(getRefundEntity(transaction)))
                            .doOnSuccess(success -> auditService.sendMessage(transaction))
                            .then();
                  }
              });
    }


    private Mono<Void> validateOperationTypePurchase(String operationType){
        log.info("Starting validateOperationTypePurchase");
        if (!"PURCHASE".equals(operationType)) {
            log.error("Invalid operation for this operationType: {}", operationType);
            return Mono.error(new BusinessException(
                  String.format("Invalid operation for this operationType: %s", operationType),
                  HttpStatus.UNPROCESSABLE_ENTITY));
        }
        return Mono.empty();
    }

    private static TransactionEntity getTransactionEntity(TransactionEntity transaction){
        return TransactionEntity.builder()
              .operationType(transaction.getOperationType())
              .amount(transaction.getAmount())
              .balance(transaction.getBalance())
              .userId(transaction.getUserId())
              .dateTime(LocalDateTime.now())
              .build();
    }

    private static RefundEntity getRefundEntity(TransactionEntity transaction){
        return RefundEntity.builder()
              .transactionId(transaction.getId())
              .userId(transaction.getUserId())
              .amount(transaction.getAmount())
              .dateTime(LocalDateTime.now())
              .build();
    }
}
