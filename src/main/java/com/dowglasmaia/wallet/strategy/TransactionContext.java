package com.dowglasmaia.wallet.strategy;

import com.dowglasmaia.wallet.exeptions.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Log4j2
@Component
public class TransactionContext {

    private final TransactionStrategy depositStrategy;
    private final TransactionStrategy purchaseStrategy;
    private final TransactionStrategy withdrawalStrategy;
    private final TransactionStrategy refundStrategy;

    @Autowired
    public TransactionContext(
          @Qualifier("depositStrategy") TransactionStrategy depositStrategy,
          @Qualifier("purchaseStrategy") TransactionStrategy purchaseStrategy,
          @Qualifier("withdrawalStrategy") TransactionStrategy withdrawalStrategy,
          @Qualifier("refundStrategy") TransactionStrategy refundStrategy
    ){
        this.depositStrategy = depositStrategy;
        this.purchaseStrategy = purchaseStrategy;
        this.withdrawalStrategy = withdrawalStrategy;
        this.refundStrategy = refundStrategy;
    }


    /**
     * Método para executar a estratégia de transação com base no tipo de operação.
     *
     * @param operationType Tipo de operação da transação (DEPOSIT, PURCHASE, WITHDRAWAL, REFUND).
     * @param currentBalance Saldo atual da conta.
     * @param amount Valor da transação a ser executada.
     * @return O novo saldo após a execução da estratégia de transação.
     * @throws BusinessException Exceção lançada se o tipo de operação não for suportado.
     */
    public BigDecimal executeStrategy(String operationType, BigDecimal currentBalance, BigDecimal amount){
        log.info("Start Method executeStrategy with operationType: " + operationType);

        switch (operationType) {
            case "DEPOSIT":
                return depositStrategy.calculateNewBalance(currentBalance, amount);
            case "PURCHASE":
                return purchaseStrategy.calculateNewBalance(currentBalance, amount);
            case "WITHDRAWAL":
                return withdrawalStrategy.calculateNewBalance(currentBalance, amount);
            case "REFUND":
                return refundStrategy.calculateNewBalance(currentBalance, amount);
            default:
                throw new BusinessException("Unsupported operation type", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
