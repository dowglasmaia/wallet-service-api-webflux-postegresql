package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.messagemodel.TransactionMessage;
import com.dowglasmaia.wallet.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {

    private final KafkaTemplate<String, Serializable> jsonKafkaTemplate;

    private final String TOPIC = "audit-transaction-topic";

    @Retryable(
          value = {DataAccessException.class},
          maxAttempts = 3,
          backoff = @Backoff(delay = 2000)
    )
    @Override
    public void sendMessage(TransactionEntity transaction){
        log.info("sendMessage: {}", transaction);
        var transactionMessage = TransactionMessage.builder()
              .transactionId(transaction.getId())
              .userId(transaction.getUserId())
              .operationType(transaction.getOperationType())
              .dateTime(transaction.getDateTime())
              .build();
        try {
            jsonKafkaTemplate.send(TOPIC, transactionMessage);

        } catch (Exception e) {
            log.error("Unexpected error sending message: {}", transactionMessage, e);
        }
    }

}
