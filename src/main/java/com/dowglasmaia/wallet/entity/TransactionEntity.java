package com.dowglasmaia.wallet.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("transactions")
public class TransactionEntity {

    @Id
    private String id;

    @NotBlank
    private String userId;

    private BigDecimal balance;

    @NotBlank
    private BigDecimal amount;

    @NotBlank
    private String operationType;

    private LocalDateTime dateTime;
}
