package com.hritvik.BankingBackend.repository;

import com.hritvik.BankingBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);

    boolean existsByAccountNumber(String accountNumber);

    User findByAccountNumber(String accountNumber);
}
