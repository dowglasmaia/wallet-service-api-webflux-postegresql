package com.dowglasmaia.wallet.controller;

import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.mockbuilder.MockBuilder;
import com.dowglasmaia.wallet.service.CreateTransactionService;
import com.dowglasmaia.wallet.service.CreditAdjustmentTransactionService;
import com.dowglasmaia.wallet.service.FindBalanceService;
import com.dowglasmaia.wallet.service.TransactionsExtractService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.dowglasmaia.wallet.mockbuilder.MockBuilder.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(value = "test")
@WebFluxTest(TransactionController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class TransactionControllerTest {

    final String BASE_PATH = "/api/v1/wallet";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private CreateTransactionService createTransactionService;

    @MockBean
    private CreditAdjustmentTransactionService adjustmentTransactionService;

    @MockBean
    private FindBalanceService findBalanceService;

    @MockBean
    private TransactionsExtractService transactionsExtractService;


    @Test
    @DisplayName("Should perform POST /transaction?operationType=DEPOSIT")
    void shouldCreateTransactionSuccessfully() {
        var responseMock = toAccountEntityMock();
        String queryParam = "DEPOSIT";

        when(createTransactionService.create(any()))
              .thenReturn(Mono.just(responseMock));


        WebTestClient.ResponseSpec response = webTestClient.post()
              .uri(uriBuilder -> uriBuilder.path(BASE_PATH +"/transaction")
                    .queryParam("operationType", queryParam)
                    .build())
              .bodyValue(toTransactionRequestMock())
              .exchange();


        response.expectStatus().isCreated()
              .expectBody()
              .jsonPath("$.transaction_id").isEqualTo(responseMock.getId().toString());
    }

    @Test
    @DisplayName("Should handle exception during transaction creation")
    void shouldHandleExceptionDuringTransactionCreation() {

        String queryParam = "SALE";
        when(createTransactionService.create(any()))
              .thenReturn(Mono.error(new BusinessException("Invalid operation type: " + queryParam, HttpStatus.BAD_REQUEST)));

        WebTestClient.ResponseSpec response = webTestClient.post()
              .uri(uriBuilder -> uriBuilder.path(BASE_PATH +"/transaction")
                    .queryParam("operationType", queryParam)
                    .build())
              .bodyValue(MockBuilder.toTransactionRequestMock())
              .exchange();


        response.expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should perform GET /balance/{userId} and return balance successfully")
    void shouldGetBalanceSuccessfully() {
        var accountMock = MockBuilder.toAccountEntityMock();
        String queryParam = accountMock.getUserId();

        when(findBalanceService.findByUserId(anyString()))
              .thenReturn(Mono.just(accountMock));

        WebTestClient.ResponseSpec response = webTestClient.get()
              .uri(uriBuilder -> uriBuilder.path(BASE_PATH +"/balance")
                    .queryParam("user_id", queryParam)
                    .build())

              .exchange();


        response.expectStatus().isOk()
              .expectBody()
              .jsonPath("$.user_id").isEqualTo(accountMock.getUserId());
    }


}

