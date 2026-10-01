package com.fincore.transactionservice.event;

public class WalletTransactionResult {

    private Long transactionId;
    private String status;
    private String message;

    public WalletTransactionResult() {
    }

    public WalletTransactionResult(
            Long transactionId,
            String status,
            String message) {

        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}