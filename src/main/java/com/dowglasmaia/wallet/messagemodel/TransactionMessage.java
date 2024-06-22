package com.dowglasmaia.wallet.messagemodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionMessage implements Serializable {

    private String userId;
    private String transactionId;
    private String operationType;
    private LocalDateTime dateTime;

}
