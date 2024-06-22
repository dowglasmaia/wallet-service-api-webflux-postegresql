package com.dowglasmaia.wallet.service;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.messagemodel.TransactionMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuditService {

    private final KafkaTemplate<String, Serializable> jsonKafkaTemplate;

    private final String TOPIC = "audit-transaction-topic";

    @SneakyThrows
    public void sendMessage(TransactionEntity transaction){
        log.info("sendMessage: {}",transaction);
        var transactionMessage = TransactionMessage.builder()
              .transactionId(transaction.getId())
              .userId(transaction.getUserId())
              .operationType(transaction.getOperationType())
              .dateTime(transaction.getDateTime())
              .build();

        jsonKafkaTemplate.send(TOPIC, transactionMessage);
    }
}
