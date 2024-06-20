package com.dowglasmaia.wallet.controller.mapper;

import br.com.dowglasmaia.openapi.model.TransactionIdResponse;
import br.com.dowglasmaia.openapi.model.TransactionRequest;
import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.entity.TransactionTypeEnum;
import com.dowglasmaia.wallet.exeptions.BusinessException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
}
