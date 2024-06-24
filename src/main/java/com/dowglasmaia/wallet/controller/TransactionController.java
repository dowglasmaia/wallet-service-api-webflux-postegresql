package com.dowglasmaia.wallet.controller;

import br.com.dowglasmaia.openapi.api.WalletApi;
import br.com.dowglasmaia.openapi.model.*;
import com.dowglasmaia.wallet.controller.mapper.TransactionMapper;
import com.dowglasmaia.wallet.service.CreateTransactionService;
import com.dowglasmaia.wallet.service.CreditAdjustmentTransactionService;
import com.dowglasmaia.wallet.service.FindBalanceService;
import com.dowglasmaia.wallet.service.TransactionsExtractService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Log4j2
@RestController
public class TransactionController implements BaseController, WalletApi {


    private final CreateTransactionService createTransactionService;
    private final CreditAdjustmentTransactionService adjustmentTransactionService;
    private final FindBalanceService findBalanceService;
    private final TransactionsExtractService transactionsExtractService;

    @Autowired
    public TransactionController(
          CreateTransactionService createTransactionService,
          CreditAdjustmentTransactionService adjustmentTransactionService,
          FindBalanceService findBalanceService,
          TransactionsExtractService transactionsExtractService
    ){
        this.createTransactionService = createTransactionService;
        this.adjustmentTransactionService = adjustmentTransactionService;
        this.findBalanceService = findBalanceService;
        this.transactionsExtractService = transactionsExtractService;
    }

    @Override
    public Mono<ResponseEntity<Void>> cancelTransaction(Mono<CancelTransactionRequest> cancelTransactionRequest, ServerWebExchange exchange){
        log.info("Start endpoint cancelTransaction");

        return cancelTransactionRequest
              .flatMap(request -> adjustmentTransactionService.processCancellationAndRefund(request.getUserId(), request.getTransactionId()))
              .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Override
    public Mono<ResponseEntity<TransactionIdResponse>> createTransaction(
          String operationType,
          Mono<TransactionRequest> transactionRequest,
          ServerWebExchange exchange
    ){
        log.info("Start endpoint createTransaction");

        return TransactionMapper.toTransactionEntity(transactionRequest, operationType)
              .flatMap(createTransactionService::create)
              .map(TransactionMapper::toTransactionIdResponse)
              .map(response -> ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(String userId, ServerWebExchange exchange){
        log.info("Start endpoint getBalance");
        return findBalanceService.findByUserId(userId)
              .map(response -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(TransactionMapper.toBalanceResponse(response)));
    }


    @Override
    public Mono<ResponseEntity<StatementResponse>> getStatement(String userId, LocalDate startDate, LocalDate endDate, ServerWebExchange exchange){
        log.info("Start endpoint getStatement");
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return transactionsExtractService.findStatementByUserId(userId, startDateTime, endDateTime)
              .collectList()
              .flatMap(TransactionMapper::toStatementResponse)
              .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
