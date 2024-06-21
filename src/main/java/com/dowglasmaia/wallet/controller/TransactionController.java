package com.dowglasmaia.wallet.controller;

import br.com.dowglasmaia.openapi.api.WalletApi;
import br.com.dowglasmaia.openapi.model.BalanceResponse;
import br.com.dowglasmaia.openapi.model.StatementResponse;
import br.com.dowglasmaia.openapi.model.TransactionIdResponse;
import br.com.dowglasmaia.openapi.model.TransactionRequest;
import com.dowglasmaia.wallet.controller.mapper.TransactionMapper;
import com.dowglasmaia.wallet.service.TransactionService;
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


    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @Override
    public Mono<ResponseEntity<TransactionIdResponse>> createTransaction(
          String operationType,
          Mono<TransactionRequest> transactionRequest,
          ServerWebExchange exchange
    ){
        log.info("Start endpoint createTransaction");

        return TransactionMapper.toTransactionEntity(transactionRequest, operationType)
              .flatMap(transactionService::create)
              .map(TransactionMapper::toTransactionIdResponse)
              .map(response -> ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(String userId, ServerWebExchange exchange){
        log.info("Start endpoint getBalance");
        return transactionService.getBalanceByUserId(userId)
              .map(response -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(TransactionMapper.toBalanceResponse(response)));
    }


    @Override
    public Mono<ResponseEntity<StatementResponse>> getStatement(String userId, LocalDate startDate, LocalDate endDate, ServerWebExchange exchange){
        log.info("Start endpoint getStatement");
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return transactionService.getStatementgetByUserId(userId, startDateTime, endDateTime)
              .collectList()
              .flatMap(TransactionMapper::toStatementResponse)
              .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}
