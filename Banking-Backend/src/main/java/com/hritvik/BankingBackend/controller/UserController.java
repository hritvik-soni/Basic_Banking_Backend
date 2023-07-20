package com.hritvik.BankingBackend.controller;

import com.hritvik.BankingBackend.model.Transaction;
import com.hritvik.BankingBackend.model.dto.*;
import com.hritvik.BankingBackend.service.StatementService;
import com.hritvik.BankingBackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    StatementService statementService;

    @PostMapping
    public BankResponse createAccount(@Valid @RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankTransferResponse transferAmount(@RequestBody TransferRequest transferRequest){
        return userService.transferAmount(transferRequest);
    }

    @GetMapping("statement")
    public BankResponse generateStatement(@RequestParam String accountNumber ,
                                               @RequestParam String startDate ,
                                               @RequestParam String endDate){


        return statementService.generateStatement(accountNumber,startDate,endDate);
    }


}
