package com.fincore.transactionservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fincore.transactionservice.entity.Transaction;
import com.fincore.transactionservice.kafka.TransactionKafkaProducer;
import com.fincore.transactionservice.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionKafkaProducer transactionKafkaProducer;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {

        transaction = new Transaction();

        transaction.setUserId(42L);
        transaction.setUserId(2L);
        transaction.setWalletId(2L);
        transaction.setType("DEPOSIT");
        transaction.setAmount(new BigDecimal("500"));
        transaction.setCurrency("USD");
        transaction.setStatus("PENDING");
    }

    @Test
    void shouldGetTransactionById() {

        when(transactionRepository.findById(42L))
                .thenReturn(Optional.of(transaction));

        Transaction result =
                transactionService.getTransaction(42L);

        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals(2L, result.getUserId());
        assertEquals(2L, result.getWalletId());
        assertEquals("DEPOSIT", result.getType());
        assertEquals(
                new BigDecimal("500"),
                result.getAmount()
        );
        assertEquals("USD", result.getCurrency());
        assertEquals("PENDING", result.getStatus());

        verify(transactionRepository)
                .findById(42L);
    }

    @Test
    void shouldUpdateTransactionStatus() {

        when(transactionRepository.findById(42L))
                .thenReturn(Optional.of(transaction));

        transactionService.updateTransactionStatus(
                42L,
                "SUCCESS"
        );

        assertEquals(
                "SUCCESS",
                transaction.getStatus()
        );

        verify(transactionRepository)
                .save(transaction);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {

        when(transactionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> transactionService.getTransaction(999L)
        );
    }
}