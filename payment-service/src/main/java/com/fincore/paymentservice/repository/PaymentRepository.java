package com.fincore.paymentservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fincore.paymentservice.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByTransactionId(Long transactionId);

    List<Payment> findByWalletId(Long walletId);
}