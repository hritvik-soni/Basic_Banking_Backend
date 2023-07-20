package com.hritvik.BankingBackend.service;

import com.hritvik.BankingBackend.model.User;
import com.hritvik.BankingBackend.model.dto.*;
import com.hritvik.BankingBackend.repository.UserRepository;
import com.hritvik.BankingBackend.service.utility.AccountUtils;
import com.hritvik.BankingBackend.service.utility.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service

public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new user into the db
         * check if user already has an account
         */
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        //Send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has been Successfully Created.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();

    }

    public BankResponse balanceEnquiry(EnquiryRequest request) {

        //check if the provided account number exists in the db

        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Send email Alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("Account Balance Credited")
                .messageBody("\nYour Account Details: \n" +
                        "Account Name: " + userToCredit.getFirstName() + " "
                        + userToCredit.getLastName() + " "
                        + "\nAccount Number: " + userToCredit.getAccountNumber()
                        + "\nBalance to Credit : " + request.getAmount()
                        + "\nUpdate Balance : " + userToCredit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }


    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            //Send email Alert
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("Account Balance Debited")
                    .messageBody("\nYour Account Details: \n" +
                            "Account Name: " + userToDebit.getFirstName() + " "
                            + userToDebit.getLastName() + " "
                            + "\nAccount Number: " + userToDebit.getAccountNumber()
                            + "\nBalance to Debit : " + request.getAmount()
                            + "\nUpdated Balance : " + userToDebit.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(emailDetails);


            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }


    public BankTransferResponse transferAmount(TransferRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isFromAccountExist = userRepository.existsByAccountNumber(request.getFromAccountNumber());
        boolean isToAccountExist = userRepository.existsByAccountNumber(request.getToAccountNumber());


        if (!isFromAccountExist) {

            return BankTransferResponse.builder()
                    .responseCodeForDebit(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessageDebit(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
//                        .accountInfoDebit(null)
//                        .responseCodeForCredit(null)
//                        .responseMessageCredit(null)
//                        .accountInfoCredit(null)
                    .build();
        }
        if (!isToAccountExist) {
            return BankTransferResponse.builder()
                    .responseCodeForDebit(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessageDebit(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
//                        .accountInfoDebit(null)
//                        .responseCodeForCredit(null)
//                        .responseMessageCredit(null)
//                        .accountInfoCredit(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getFromAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmountToTransfer().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {

            return BankTransferResponse.builder()
                    .responseCodeForDebit(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessageDebit(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfoDebit(null)
                    .responseCodeForCredit(null)
                    .responseMessageCredit(null)
                    .accountInfoCredit(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmountToTransfer()));
            userRepository.save(userToDebit);

            //Send email Alert
            EmailDetails emailDetailsDebit = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("Account Balance Debited")
                    .messageBody("\nYour Account Details: \n" +
                            "Account Name: " + userToDebit.getFirstName() + " "
                            + userToDebit.getLastName() + " "
                            + "\nAccount Number: " + userToDebit.getAccountNumber()
                            + "\nBalance to Debit : " + request.getToAccountNumber()
                            + "\nUpdated Balance : " + userToDebit.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(emailDetailsDebit);


            User userToCredit = userRepository.findByAccountNumber(request.getToAccountNumber());
            userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmountToTransfer()));
            userRepository.save(userToCredit);

            //Send email Alert
            EmailDetails emailDetailsCredit = EmailDetails.builder()
                    .recipient(userToCredit.getEmail())
                    .subject("Account Balance Credited")
                    .messageBody("\nYour Account Details: \n" +
                            "Account Name: " + userToCredit.getFirstName() + " "
                            + userToCredit.getLastName() + " "
                            + "\nAccount Number: " + userToCredit.getAccountNumber()
                            + "\nBalance to Credit : " + request.getAmountToTransfer()
                            + "\nUpdate Balance : " + userToCredit.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(emailDetailsCredit);


            return BankTransferResponse.builder()
                    .responseCodeForDebit(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessageDebit(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfoDebit(AccountInfo.builder()
                            .accountNumber(request.getFromAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .responseCodeForCredit(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                    .responseMessageCredit(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                    .accountInfoCredit(AccountInfo.builder()
                            .accountNumber(request.getToAccountNumber())
                            .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                            .accountBalance(userToCredit.getAccountBalance())
                            .build())
                    .build();


        }

    }
}
