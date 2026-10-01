package com.fincore.transactionservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fincore.transactionservice.entity.Transaction;
import com.fincore.transactionservice.event.TransactionEvent;
import com.fincore.transactionservice.kafka.TransactionKafkaProducer;
import com.fincore.transactionservice.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionKafkaProducer transactionKafkaProducer;

    public TransactionService(
            TransactionRepository transactionRepository,
            TransactionKafkaProducer transactionKafkaProducer) {

        this.transactionRepository = transactionRepository;
        this.transactionKafkaProducer = transactionKafkaProducer;
    }

    public Transaction createTransaction(
            Long userId,
            Long walletId,
            String type,
            BigDecimal amount,
            String currency) {

        validateTransaction(userId, walletId, type, amount, currency);

        Transaction transaction = new Transaction(
                userId,
                walletId,
                type,
                amount,
                currency,
                "PENDING",
                LocalDateTime.now()
        );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(
                savedTransaction.getId(),
                savedTransaction.getUserId(),
                savedTransaction.getWalletId(),
                savedTransaction.getType(),
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getStatus()
        );

        transactionKafkaProducer.sendTransactionEvent(event);

        return savedTransaction;
    }

    public Transaction getTransaction(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Transaction not found"
                        )
                );
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getWalletTransactions(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    public Transaction updateTransactionStatus(
            Long transactionId,
            String status) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"
                                )
                        );

        transaction.setStatus(status);

        return transactionRepository.save(transaction);
    }

    private void validateTransaction(
            Long userId,
            Long walletId,
            String type,
            BigDecimal amount,
            String currency) {

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User ID cannot be null"
            );
        }

        if (walletId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Wallet ID cannot be null"
            );
        }

        if (amount == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount cannot be null"
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount must be greater than zero"
            );
        }

        if (type == null || type.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type is required"
            );
        }

        if (!"DEPOSIT".equalsIgnoreCase(type)
                && !"WITHDRAW".equalsIgnoreCase(type)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type must be DEPOSIT or WITHDRAW"
            );
        }

        if (currency == null || currency.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Currency is required"
            );
        }
    }
}