package com.dowglasmaia.wallet.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Log4j2
@Component("refundStrategy")
public class RefundStrategy implements TransactionStrategy {

    @Override
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount){
        log.info("calculateNewBalance - RefundStrategy");
        return currentBalance.add(amount);
    }
}
