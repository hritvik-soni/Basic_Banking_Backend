package com.hritvik.BankingBackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransferResponse {
    private String responseCodeForDebit;
    private String responseMessageDebit;
    private AccountInfo accountInfoDebit;

    private String responseCodeForCredit;
    private String responseMessageCredit;
    private AccountInfo accountInfoCredit;
}
