package com.dowglasmaia.wallet.controller.mapper;

import br.com.dowglasmaia.openapi.model.*;
import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.enums.TransactionTypeEnum;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

public class TransactionMapper {

    public static Mono<TransactionEntity> toTransactionEntity(Mono<TransactionRequest> requestMono, String operationType){
        return requestMono
              .map(request -> {
                  try {
                      TransactionTypeEnum.valueOf(operationType);

                  } catch (IllegalArgumentException e) {
                      throw new BusinessException("Invalid operation type: " + operationType, HttpStatus.BAD_REQUEST);
                  }

                  return TransactionEntity.builder()
                        .userId(request.getUserId())
                        .operationType(operationType)
                        .amount(request.getAmount())
                        .dateTime(LocalDateTime.now())
                        .build();
              })
              .onErrorMap(e -> new BusinessException("Invalid request data", HttpStatus.BAD_REQUEST));
    }


    public static TransactionIdResponse toTransactionIdResponse(AccountEntity entity){
        TransactionIdResponse response = new TransactionIdResponse();
        response.setTransactionId(String.valueOf(entity.getId()));
        return response;
    }

    public static BalanceResponse toBalanceResponse(AccountEntity entity){
        BalanceResponse response = new BalanceResponse();
        response.setBalance(entity.getBalance());
        response.setUserId(entity.getUserId());
        return response;

    }

    public static Mono<StatementResponse> toStatementResponse(List<TransactionEntity> entities){
        StatementResponse response = new StatementResponse();

        // Converte a lista de entidades de transação para um fluxo de respostas de transação
        Flux<TransactionResponse> transactions = Flux.fromIterable(entities)
              .map(TransactionMapper::toTransactionResponse);

        return transactions.collectList()
              .map(transactionList -> {
                  response.setTransactions(transactionList);
                  response.setUserId(entities.isEmpty() ? null : entities.get(0).getUserId());
                  return response;
              });
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
