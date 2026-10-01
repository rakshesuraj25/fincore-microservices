package com.fincore.transactionservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.transactionservice.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByWalletId(Long walletId);
}