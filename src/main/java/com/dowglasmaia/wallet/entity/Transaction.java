package com.dowglasmaia.wallet.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("transactions")
public class Transaction {

    @Id
    private Long id;
    private String userId;
    private BigDecimal amount;
    private String operationType;
    private LocalDateTime dateTime;
}
