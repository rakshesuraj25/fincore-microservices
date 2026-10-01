package com.fincore.transactionservice.event;

import java.math.BigDecimal;

public class TransactionEvent {

    private Long transactionId;
    private Long userId;
    private Long walletId;
    private String type;
    private BigDecimal amount;
    private String currency;
    private String status;

    public TransactionEvent() {
    }

    public TransactionEvent(
            Long transactionId,
            Long userId,
            Long walletId,
            String type,
            BigDecimal amount,
            String currency,
            String status) {

        this.transactionId = transactionId;
        this.userId = userId;
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}