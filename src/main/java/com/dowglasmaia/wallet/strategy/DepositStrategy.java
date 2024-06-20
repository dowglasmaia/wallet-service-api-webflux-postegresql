package com.dowglasmaia.wallet.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Log4j2
@Component
public class DepositStrategy implements TransactionStrategy {

    @Override
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount){
        log.info("calculateNewBalance - DepositStrategy");
        return currentBalance.add(amount);
    }
}
