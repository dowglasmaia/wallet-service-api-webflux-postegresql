package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.TransactionEntity;
import com.dowglasmaia.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static com.dowglasmaia.wallet.mockbuilder.MockBuilder.toTransactionEntityMock;
import static org.mockito.Mockito.when;

public class TransactionsExtractServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionsExtractServiceImpl transactionsExtractService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find transactions by userId and date range successfully")
    void shouldFindTransactionsByUserIdAndDateRangeSuccessfully(){

        String userId = "user-123";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<TransactionEntity> transactionMockList = List.of(toTransactionEntityMock());

        when(transactionRepository.findByUserIdAndDateTimeBetween(userId, startDate, endDate))
              .thenReturn(Flux.fromIterable(transactionMockList));

        StepVerifier
              .create(transactionsExtractService.findStatementByUserId(userId, startDate, endDate))
              .expectNext(transactionMockList.get(0))
              .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when no transactions found by userId and date range")
    void shouldReturnEmptyWhenNoTransactionsFoundByUserIdAndDateRange(){

        String userId = "user-456";
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionRepository.findByUserIdAndDateTimeBetween(userId, startDate, endDate))
              .thenReturn(Flux.empty());


        StepVerifier
              .create(transactionsExtractService.findStatementByUserId(userId, startDate, endDate))
              .verifyComplete();
    }
}
