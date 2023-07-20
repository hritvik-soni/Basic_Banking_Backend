package com.hritvik.BankingBackend.service;

import com.hritvik.BankingBackend.model.Transaction;
import com.hritvik.BankingBackend.model.dto.TransactionInfo;
import com.hritvik.BankingBackend.repository.TransactionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionalService {
    @Autowired
    TransactionalRepository transactionalRepository;

    public void saveTransactional(TransactionInfo transactionInfo){
        Transaction transaction = Transaction.builder()
                .transactionType(transactionInfo.getTransactionType())
                .accountNumber(transactionInfo.getAccountNumber())
                .amount(transactionInfo.getAmount())
                .status("SUCCESS")
                .build();
        transactionalRepository.save(transaction);
        System.out.print("Transaction saved successfully");
    }
}
