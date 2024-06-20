package com.dowglasmaia.wallet.controller;

import br.com.dowglasmaia.openapi.api.WalletApi;
import br.com.dowglasmaia.openapi.model.BalanceResponse;
import br.com.dowglasmaia.openapi.model.StatementResponse;
import br.com.dowglasmaia.openapi.model.TransactionIdResponse;
import br.com.dowglasmaia.openapi.model.TransactionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Log4j2
@RestController
public class TransactionController implements WalletApi {


    @Override
    public Mono<ResponseEntity<TransactionIdResponse>> createTransaction(String operationType, Mono<TransactionRequest> transactionRequest, ServerWebExchange exchange){
        return null;
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(String userId, ServerWebExchange exchange){
        return null;
    }

    @Override
    public Mono<ResponseEntity<StatementResponse>> getStatement(String userId, LocalDate startDate, LocalDate endDate, ServerWebExchange exchange){
        return null;
    }
}
