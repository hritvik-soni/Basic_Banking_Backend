package com.hritvik.BankingBackend.controller;

import com.hritvik.BankingBackend.model.dto.BankResponse;
import com.hritvik.BankingBackend.model.dto.CreditDebitRequest;
import com.hritvik.BankingBackend.model.dto.EnquiryRequest;
import com.hritvik.BankingBackend.model.dto.UserRequest;
import com.hritvik.BankingBackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

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


}
