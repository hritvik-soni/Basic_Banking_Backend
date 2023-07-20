package com.hritvik.BankingBackend.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String stateOfOrigin;

    @Email
    @Column(unique=true)
    private String email;
    private String phoneNumber;

}