package com.dowglasmaia.wallet.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawalStrategy implements TransactionStrategy {

    @Override
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount){

        return null;
    }

}
