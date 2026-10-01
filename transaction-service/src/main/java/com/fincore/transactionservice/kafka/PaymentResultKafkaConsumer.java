package com.fincore.transactionservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fincore.transactionservice.event.PaymentResult;
import com.fincore.transactionservice.service.TransactionService;

@Service
public class PaymentResultKafkaConsumer {

    private final TransactionService transactionService;

    public PaymentResultKafkaConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
    	    topics = "payment-results",
    	    groupId = "fincore-transaction-payment-group",
    	    containerFactory = "paymentResultKafkaListenerContainerFactory"
    	)
    public void consumePaymentResult(PaymentResult result) {

        System.out.println("========================================");
        System.out.println("Kafka Payment Result Received");
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Transaction ID: " + result.getTransactionId());
        System.out.println("Status: " + result.getStatus());
        System.out.println("Message: " + result.getMessage());

        try {
            transactionService.updateTransactionStatus(
                    result.getTransactionId(),
                    result.getStatus()
            );

            System.out.println(
                    "Transaction status updated: "
                    + result.getStatus()
            );

        } catch (Exception e) {
            System.out.println(
                    "Failed to update transaction status: "
                    + e.getMessage()
            );
        }

        System.out.println("========================================");
    }
}