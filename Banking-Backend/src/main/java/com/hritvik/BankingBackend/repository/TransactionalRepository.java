package com.hritvik.BankingBackend.repository;

import com.hritvik.BankingBackend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionalRepository extends JpaRepository<Transaction,String> {

}
