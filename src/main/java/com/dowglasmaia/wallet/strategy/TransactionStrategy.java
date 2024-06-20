package com.dowglasmaia.wallet.strategy;

import java.math.BigDecimal;

public interface TransactionStrategy {

    BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount);
}
