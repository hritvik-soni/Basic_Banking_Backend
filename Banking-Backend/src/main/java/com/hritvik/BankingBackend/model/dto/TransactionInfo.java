package com.hritvik.BankingBackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionInfo {

    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
