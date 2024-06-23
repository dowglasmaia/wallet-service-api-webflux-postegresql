package com.dowglasmaia.wallet.messagemodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionMessage implements Serializable {

    private String userId;
    private UUID transactionId;
    private String operationType;
    private LocalDateTime dateTime;

}
