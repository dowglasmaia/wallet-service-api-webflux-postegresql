package com.dowglasmaia.wallet.controller;

import br.com.dowglasmaia.openapi.api.CreditAdjustmentApi;
import br.com.dowglasmaia.openapi.model.CancelTransactionRequest;
import com.dowglasmaia.wallet.service.CreditAdjustmentTransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
public class CreditAdjustmentTransactionController implements BaseController, CreditAdjustmentApi {

    private final CreditAdjustmentTransactionService transactionService;

    @Autowired
    public CreditAdjustmentTransactionController(CreditAdjustmentTransactionService transactionService){
        this.transactionService = transactionService;
    }

    @Override
    public Mono<ResponseEntity<Void>> cancelTransaction(Mono<CancelTransactionRequest> cancelTransactionRequest, ServerWebExchange exchange){
        log.info("Start endpoint cancelTransaction");

        return cancelTransactionRequest
              .flatMap(request -> transactionService.processCancellationAndRefund(request.getUserId(), request.getTransactionId()))
              .then(Mono.just(ResponseEntity.noContent().build()));
    }

}
