package com.fincore.transactionservice.event;

public class PaymentResult {

    private Long paymentId;
    private Long transactionId;
    private String status;
    private String message;

    public PaymentResult() {
    }

    public PaymentResult(Long paymentId, Long transactionId,
                         String status, String message) {
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
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