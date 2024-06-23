package com.dowglasmaia.wallet.controller.mapper;

import br.com.dowglasmaia.openapi.model.*;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.enums.TransactionTypeEnum;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

public class TransactionMapper {

    public static Mono<TransactionEntity> toTransactionEntity(Mono<TransactionRequest> requestMono, String operationType){
        return requestMono
              .map(request -> {
                  TransactionTypeEnum transactionType;
                  try {
                      transactionType = TransactionTypeEnum.valueOf(operationType);
                  } catch (IllegalArgumentException e) {
                      throw new BusinessException("Invalid operation type: " + operationType, HttpStatus.BAD_REQUEST);
                  }

                  return TransactionEntity.builder()
                        .amount(request.getAmount())
                        .userId(request.getUserId())
                        .dateTime(LocalDateTime.now())
                        .operationType(transactionType.name())
                        .build();
              })
              .onErrorMap(e -> new BusinessException("Invalid request data", HttpStatus.BAD_REQUEST));
    }


    public static TransactionIdResponse toTransactionIdResponse(TransactionEntity entity){
        TransactionIdResponse response = new TransactionIdResponse();
        response.setTransactionId(String.valueOf(entity.getId()));
        return response;
    }

    public static BalanceResponse toBalanceResponse(TransactionEntity entity){
        BalanceResponse response = new BalanceResponse();
        response.setBalance(entity.getBalance());
        response.setUserId(entity.getUserId());
        return response;

    }

    public static Mono<StatementResponse> toStatementResponse(List<TransactionEntity> entities){
        StatementResponse response = new StatementResponse();

        Flux<TransactionEntity> entityFlux = Flux.fromIterable(entities);

        Flux<TransactionResponse> transactions = entityFlux.map(TransactionMapper::toTransactionResponse);

        // Filtra e agrega entradas e saídas
        Mono<BigDecimal> inputs = entityFlux
              .filter(entity -> entity.getOperationType().equals(TransactionTypeEnum.DEPOSIT.name()))
              .map(TransactionEntity::getAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

        Mono<BigDecimal> outputs = entityFlux
              .filter(entity -> !entity.getOperationType().equals(TransactionTypeEnum.DEPOSIT.name()))
              .map(TransactionEntity::getAmount)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcula o saldo e monta a resposta
        return inputs.zipWith(outputs, BigDecimal::subtract)
              .flatMap(balance -> transactions.collectList()
                    .map(transactionList -> {
                        response.setTransactions(transactionList);
                        response.setUserId(entities.isEmpty() ? null : entities.get(0).getUserId());
                        response.setBalance(balance);
                        return response;
                    }));
    }

    private static TransactionResponse toTransactionResponse(TransactionEntity entity){
        ZoneId zoneId = ZoneId.systemDefault();
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(entity.getDateTime());
        OffsetDateTime offsetDateTime = entity.getDateTime().atOffset(zoneOffset);

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(entity.getId().toString());
        transactionResponse.setAmount(entity.getAmount());
        transactionResponse.setOperationType(entity.getOperationType());
        transactionResponse.setDateTime(offsetDateTime);
        return transactionResponse;
    }

}
