package com.hritvik.BankingBackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private String fromAccountNumber;
    private BigDecimal amountToTransfer;
    private String ToAccountNumber;


}
