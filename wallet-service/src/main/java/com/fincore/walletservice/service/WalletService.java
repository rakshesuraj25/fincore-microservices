package com.fincore.walletservice.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fincore.walletservice.entity.Wallet;
import com.fincore.walletservice.repository.WalletRepository;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(Long userId, String currency) {

        if (walletRepository.findByUserIdAndCurrency(userId, currency).isPresent()) {
            throw new RuntimeException("Wallet already exists for this currency");
        }

        Wallet wallet = new Wallet(
                userId,
                currency,
                BigDecimal.ZERO
        );

        return walletRepository.save(wallet);
    }

    public Wallet getWallet(Long walletId) {

        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public List<Wallet> getWalletsByUser(Long userId) {

        return walletRepository.findByUserId(userId);
    }

    public Wallet addMoney(Long walletId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWallet(walletId);

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        return walletRepository.save(wallet);
    }

    public Wallet withdrawMoney(Long walletId, BigDecimal amount) {

        validateAmount(amount);

        Wallet wallet = getWallet(walletId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        return walletRepository.save(wallet);
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null) {
            throw new RuntimeException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero"
            );
        }
    }
}