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
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("refund")
public class RefundEntity {

    @Id
    private UUID id;

    @NotBlank
    private String userId;

    @NotBlank
    private UUID transactionId;

    @NotBlank
    private BigDecimal amount;

    private LocalDateTime dateTime;
}
