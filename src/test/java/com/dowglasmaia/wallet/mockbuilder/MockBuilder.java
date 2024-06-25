package com.dowglasmaia.wallet.mockbuilder;

import br.com.dowglasmaia.openapi.model.TransactionIdResponse;
import br.com.dowglasmaia.openapi.model.TransactionRequest;
import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class MockBuilder {

    public static AccountEntity toAccountEntityMock(){
        TransactionIdResponse response = new TransactionIdResponse();
        response.setTransactionId("response-id");
        return AccountEntity.builder()
              .id(UUID.randomUUID())
              .userId("user_01523")
              .build();
    }

    public static TransactionRequest toTransactionRequestMock(){
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setUserId("user-123");
        return request;
    }

    public static TransactionEntity toTransactionEntityMock(){
        TransactionEntity entity = new TransactionEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user-123");
        entity.setAmount(BigDecimal.valueOf(500));
        entity.setOperationType("DEPOSIT");
        entity.setDateTime(LocalDateTime.now().minusDays(10));
        return entity;
    }

}
