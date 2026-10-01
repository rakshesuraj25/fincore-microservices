package com.fincore.paymentservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fincore.paymentservice.entity.Payment;
import com.fincore.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long transactionId,
            @RequestParam Long userId,
            @RequestParam Long walletId,
            @RequestParam BigDecimal amount,
            @RequestParam String currency,
            @RequestParam String paymentMethod) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(
                        transactionId,
                        userId,
                        walletId,
                        amount,
                        currency,
                        paymentMethod
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPayment(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                paymentService.getUserPayments(userId)
        );
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Payment>> getTransactionPayments(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                paymentService.getTransactionPayments(transactionId)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status)
        );
    }
}