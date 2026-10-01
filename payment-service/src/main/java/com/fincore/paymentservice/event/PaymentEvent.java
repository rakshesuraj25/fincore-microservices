package com.fincore.paymentservice.event;

import java.math.BigDecimal;

public class PaymentEvent {

    private Long paymentId;
    private Long transactionId;
    private Long userId;
    private Long walletId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;

    public PaymentEvent() {
    }

    public PaymentEvent(
            Long paymentId,
            Long transactionId,
            Long userId,
            Long walletId,
            BigDecimal amount,
            String currency,
            String paymentMethod,
            String status) {

        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}