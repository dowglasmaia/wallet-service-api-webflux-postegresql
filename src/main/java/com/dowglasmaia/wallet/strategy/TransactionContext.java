package com.dowglasmaia.wallet.strategy;

import com.dowglasmaia.wallet.exeptions.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Log4j2
@Component
public class TransactionContext {

    private final TransactionStrategy depositStrategy;
    private final TransactionStrategy purchaseStrategy;
    private final TransactionStrategy withdrawalStrategy;

    @Autowired
    public TransactionContext(
          TransactionStrategy depositStrategy,
          TransactionStrategy purchaseStrategy,
          TransactionStrategy withdrawalStrategy
    ){
        this.depositStrategy = depositStrategy;
        this.purchaseStrategy = purchaseStrategy;
        this.withdrawalStrategy = withdrawalStrategy;
    }

    public BigDecimal executeStrategy(String operationType, BigDecimal currentBalance, BigDecimal amount){
        log.info("Start Method executeStrategy with operationType: " + operationType);

        switch (operationType) {
            case "DEPOSIT":
                return depositStrategy.calculateNewBalance(currentBalance, amount);
            case "PURCHASE":
                return purchaseStrategy.calculateNewBalance(currentBalance, amount);
            case "WITHDRAWAL":
                return withdrawalStrategy.calculateNewBalance(currentBalance, amount);
            default:
                throw new BusinessException("Unsupported operation type", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
