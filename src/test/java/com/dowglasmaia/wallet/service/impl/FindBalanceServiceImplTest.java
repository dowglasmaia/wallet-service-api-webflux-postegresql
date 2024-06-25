package com.dowglasmaia.wallet.service.impl;

import com.dowglasmaia.wallet.entity.AccountEntity;
import com.dowglasmaia.wallet.mockbuilder.MockBuilder;
import com.dowglasmaia.wallet.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

public class FindBalanceServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FindBalanceServiceImpl findBalanceService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find account by userId successfully")
    void shouldFindAccountByUserIdSuccessfully(){
        String userId = "user-123";
        AccountEntity accountMock = MockBuilder.toAccountEntityMock();

        when(accountRepository.findByUserId(userId))
              .thenReturn(Mono.just(accountMock));

        StepVerifier
              .create(findBalanceService.findByUserId(userId))
              .expectNext(accountMock)
              .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when account not found by userId")
    void shouldReturnEmptyWhenAccountNotFoundByUserId(){
        String userId = "user-456";
        when(accountRepository.findByUserId(userId))
              .thenReturn(Mono.empty());

        StepVerifier
              .create(findBalanceService.findByUserId(userId))
              .verifyComplete();
    }
}
