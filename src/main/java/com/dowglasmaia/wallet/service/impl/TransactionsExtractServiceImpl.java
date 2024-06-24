package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import com.dowglasmaia.wallet.service.TransactionsExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Service
public class TransactionsExtractServiceImpl implements TransactionsExtractService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionsExtractServiceImpl(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Flux<TransactionEntity> findStatementByUserId(String userId, LocalDateTime startDate, LocalDateTime endDate){
        return transactionRepository.findByUserIdAndDateTimeBetween(userId, startDate, endDate);
    }
}
