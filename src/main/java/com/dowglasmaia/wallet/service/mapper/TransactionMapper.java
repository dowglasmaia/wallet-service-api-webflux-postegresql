package com.dowglasmaia.wallet.service.mapper;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionMapper {

    public static TransactionEntity toTransactionEntity(AccountEntity account, String operationType, BigDecimal amount){
        return TransactionEntity.builder()
              .operationType(operationType)
              .userId(account.getUserId())
              .amount(amount)
              .dateTime(LocalDateTime.now())
              .build();
    }
}
