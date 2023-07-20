package com.hritvik.BankingBackend.service;

import com.hritvik.BankingBackend.model.Transaction;
import com.hritvik.BankingBackend.model.User;
import com.hritvik.BankingBackend.model.dto.BankResponse;
import com.hritvik.BankingBackend.model.dto.EmailDetails;
import com.hritvik.BankingBackend.repository.TransactionalRepository;
import com.hritvik.BankingBackend.repository.UserRepository;
import com.hritvik.BankingBackend.service.utility.AccountUtils;
import com.hritvik.BankingBackend.service.utility.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service

public class StatementService {

    @Autowired
    TransactionalRepository transactionalRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;


    public BankResponse generateStatement(String accountNumber , String startDate ,String endDate){


        boolean isAccountExist = userRepository.existsByAccountNumber(accountNumber);
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        LocalDate start =LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end =LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);


        List<Transaction> transactionList= transactionalRepository.findAll().stream()
                .filter(transaction ->transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start))
                .filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();


        User userStatement = userRepository.findByAccountNumber(accountNumber);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userStatement.getEmail())
                .subject("Bank Transaction Statement")
                .messageBody("\nYour Account Details: \n" +
                        "Account Name: " + userStatement.getFirstName() + " "
                        + userStatement.getLastName() + " "
                        + "\nAccount Number: " + userStatement.getAccountNumber()
                        + "\nUpdated Balance : " + userStatement.getAccountBalance()
                        + "\nBank Statement : \n " + transactionList)
                        .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_STATEMENT_SENT_CODE)
                .responseMessage(AccountUtils.ACCOUNT_STATEMENT_SENT_MESSAGE)
                .accountInfo(null)
                .build();
    }


}
