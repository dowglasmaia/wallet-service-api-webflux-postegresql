package com.dowglasmaia.wallet.mockbuilder;

import br.com.dowglasmaia.openapi.model.TransactionIdResponse;
import br.com.dowglasmaia.openapi.model.TransactionRequest;
import com.dowglasmaia.wallet.entity.AccountEntity;

import java.math.BigDecimal;
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

    public static TransactionRequest toTransactionRequestMock() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(200));
        request.setUserId("user-123");
        return request;
    }

}
