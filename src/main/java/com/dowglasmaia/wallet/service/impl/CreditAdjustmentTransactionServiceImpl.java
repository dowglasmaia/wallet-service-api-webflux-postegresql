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

    /**
     * Método para processar o cancelamento e o reembolso de uma transação.
     *
     * @param userId ID do usuário associado à transação.
     * @param transactionId ID da transação a ser cancelada e reembolsada.
     * @return Um Mono<Void> indicando a conclusão do processo.
     */
    @Transactional // Garante que todo o método seja executado em uma transação
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


    /**
     * Método para encontrar uma transação pelo ID do usuário.
     *
     * @param userId ID do usuário associado à transação.
     * @param transactionId ID da transação a ser encontrada.
     * @return Um Mono<TransactionEntity> representando a transação encontrada.
     */
    private Mono<TransactionEntity> findTransactionByIdAndUser(String userId, String transactionId){
        return transactionRepository.findByIdAndUserId(UUID.fromString(transactionId), userId)
              .switchIfEmpty(Mono.error(new BusinessException("Transaction not found", HttpStatus.NOT_FOUND)))
              .flatMap(this::validatePurchaseTransaction)
              .doFirst(() -> log.info("Starting findTransactionByIdAndUser"))
              .doOnSubscribe(subscription -> log.info("Validating Refund Transaction"));
    }


    /**
     * Método para validar uma transação de compra.
     *
     * @param transaction Transação a ser validada.
     * @return Um Mono<TransactionEntity> representando a transação válida.
     */
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

    /**
     * Método para lidar com o processo de reembolso.
     *
     * @param transaction Transação a ser reembolsada.
     * @param account Conta associada à transação.
     * @return Um Mono<Void> indicando a conclusão do processo.
     */
    private Mono<Void> handleRefund(TransactionEntity transaction, AccountEntity account){
        return refundRepository.existsByTransactionIdAndUserId(transaction.getId(), transaction.getUserId())
              .flatMap(exists -> {
                  if (exists) {
                      String errorMessage = "Operation not performed; it has already been processed for this transaction";
                      log.error(errorMessage);

                      // Lança uma exceção se o reembolso já tiver sido processado para esta transação
                      return Mono.error(new BusinessException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY));
                  } else {
                      return performRefund(transaction, account);
                  }
              })
              .doFirst(() -> log.info("Starting handleRefund"));
    }


    /**
     * Método para executar o reembolso de uma transação.
     *
     * @param transaction Transação a ser reembolsada.
     * @param account Conta associada à transação.
     * @return Um Mono<Void> indicando a conclusão do processo de reembolso.
     */
    private Mono<Void> performRefund(TransactionEntity transaction, AccountEntity account){
        return Mono.defer(() -> {
            TransactionEntity refundTransaction = createRefundTransaction(transaction);
            BigDecimal newBalance = transactionContext.executeStrategy(
                  refundTransaction.getOperationType(),
                  account.getBalance(),
                  refundTransaction.getAmount()
            );

            account.setBalance(newBalance);

            // Salva a transação de reembolso, atualiza a conta e salva a entidade de reembolso
            return Mono.when(
                  transactionRepository.save(refundTransaction),
                  accountRepository.save(account),
                  refundRepository.save(createRefundEntity(transaction))
            ).doOnSuccess(unused -> auditService.sendMessage(refundTransaction));
        });
    }


    /**
     * Método para criar uma transação de reembolso com base em uma transação original.
     *
     * @param transaction Transação original a ser reembolsada.
     * @return Uma nova entidade de transação representando o reembolso.
     */
    private TransactionEntity createRefundTransaction(TransactionEntity transaction){
        log.info("Creating refund transaction");
        return TransactionEntity.builder()
              .userId(transaction.getUserId())
              .operationType(TransactionTypeEnum.REFUND.name())
              .amount(transaction.getAmount())
              .dateTime(LocalDateTime.now())
              .build();
    }


    /**
     * Método para criar uma entidade de reembolso com base em uma transação de reembolso.
     *
     * @param refundTransaction Transação de reembolso a ser convertida em entidade de reembolso.
     * @return Uma nova entidade de reembolso.
     */
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
