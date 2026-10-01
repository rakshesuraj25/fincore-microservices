package com.fincore.transactionservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fincore.transactionservice.entity.Transaction;
import com.fincore.transactionservice.service.TransactionService;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(
            @RequestParam Long userId,
            @RequestParam Long walletId,
            @RequestParam String type,
            @RequestParam BigDecimal amount,
            @RequestParam String currency) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        transactionService.createTransaction(
                                userId,
                                walletId,
                                type,
                                amount,
                                currency
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transactionService.getTransaction(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                transactionService.getUserTransactions(userId)
        );
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transaction>> getWalletTransactions(
            @PathVariable Long walletId) {

        return ResponseEntity.ok(
                transactionService.getWalletTransactions(walletId)
        );
    }
}