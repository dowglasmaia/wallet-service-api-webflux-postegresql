package com.dowglasmaia.wallet.strategy;

import com.dowglasmaia.wallet.exeptions.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Log4j2
@Component
public class WithdrawalStrategy implements TransactionStrategy {

    @Override
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount){
        log.info("calculateNewBalance - WithdrawalStrategy");
        if (currentBalance.compareTo(amount) >= 0)
            return currentBalance.subtract(amount);
        else
            log.error("Insufficient balance for withdrawal");
        throw new BusinessException("Insufficient balance for withdrawal", HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
