package com.fincore.walletservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fincore.walletservice.entity.Wallet;
import com.fincore.walletservice.service.WalletService;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(
            @RequestParam Long userId,
            @RequestParam String currency) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(walletService.createWallet(userId, currency));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWallet(
            @PathVariable Long walletId) {

        return ResponseEntity.ok(
                walletService.getWallet(walletId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wallet>> getWalletsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                walletService.getWalletsByUser(userId)
        );
    }

    @PutMapping("/{walletId}/deposit")
    public ResponseEntity<Wallet> deposit(
            @PathVariable Long walletId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                walletService.addMoney(walletId, amount)
        );
    }

    @PutMapping("/{walletId}/withdraw")
    public ResponseEntity<Wallet> withdraw(
            @PathVariable Long walletId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                walletService.withdrawMoney(walletId, amount)
        );
    }
}