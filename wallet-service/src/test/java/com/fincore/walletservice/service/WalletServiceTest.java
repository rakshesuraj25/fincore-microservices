package com.fincore.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fincore.walletservice.entity.Wallet;
import com.fincore.walletservice.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    void setUp() {

        wallet = new Wallet();

        wallet.setUserId(2L);
        wallet.setCurrency("USD");
        wallet.setBalance(new BigDecimal("1000"));
    }

    @Test
    void shouldCreateWallet() {

        when(walletRepository.findByUserIdAndCurrency(2L, "USD"))
                .thenReturn(Optional.empty());

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result =
                walletService.createWallet(2L, "USD");

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("USD", result.getCurrency());
        assertEquals(
                BigDecimal.ZERO,
                result.getBalance()
        );

        verify(walletRepository)
                .findByUserIdAndCurrency(2L, "USD");

        verify(walletRepository)
                .save(any(Wallet.class));
    }

    @Test
    void shouldThrowExceptionWhenWalletAlreadyExists() {

        when(walletRepository.findByUserIdAndCurrency(2L, "USD"))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                RuntimeException.class,
                () -> walletService.createWallet(2L, "USD")
        );
    }

    @Test
    void shouldGetWallet() {

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        Wallet result =
                walletService.getWallet(1L);

        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("USD", result.getCurrency());
        assertEquals(
                new BigDecimal("1000"),
                result.getBalance()
        );

        verify(walletRepository)
                .findById(1L);
    }

    @Test
    void shouldAddMoney() {

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(wallet))
                .thenReturn(wallet);

        Wallet result =
                walletService.addMoney(
                        1L,
                        new BigDecimal("500")
                );

        assertEquals(
                new BigDecimal("1500"),
                result.getBalance()
        );

        verify(walletRepository)
                .findById(1L);

        verify(walletRepository)
                .save(wallet);
    }

    @Test
    void shouldWithdrawMoney() {

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(wallet))
                .thenReturn(wallet);

        Wallet result =
                walletService.withdrawMoney(
                        1L,
                        new BigDecimal("300")
                );

        assertEquals(
                new BigDecimal("700"),
                result.getBalance()
        );

        verify(walletRepository)
                .findById(1L);

        verify(walletRepository)
                .save(wallet);
    }

    @Test
    void shouldThrowExceptionForInsufficientBalance() {

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                RuntimeException.class,
                () -> walletService.withdrawMoney(
                        1L,
                        new BigDecimal("2000")
                )
        );
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() {

        assertThrows(
                RuntimeException.class,
                () -> walletService.addMoney(
                        1L,
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldGetWalletsByUser() {

        Wallet wallet2 = new Wallet();

        wallet2.setUserId(2L);
        wallet2.setCurrency("EUR");
        wallet2.setBalance(new BigDecimal("500"));

        when(walletRepository.findByUserId(2L))
                .thenReturn(Arrays.asList(wallet, wallet2));

        List<Wallet> result =
                walletService.getWalletsByUser(2L);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(walletRepository)
                .findByUserId(2L);
    }
}