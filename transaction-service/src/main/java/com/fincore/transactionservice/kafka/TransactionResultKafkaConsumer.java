package com.fincore.transactionservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fincore.transactionservice.event.WalletTransactionResult;
import com.fincore.transactionservice.service.TransactionService;

@Service
public class TransactionResultKafkaConsumer {

    private final TransactionService transactionService;

    public TransactionResultKafkaConsumer(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "transaction-results",
            groupId = "fincore-transaction-group"
    )
    public void consumeTransactionResult(
            WalletTransactionResult result) {

        System.out.println("========================================");
        System.out.println("Kafka Transaction Result Received");
        System.out.println(
                "Transaction ID: " + result.getTransactionId()
        );
        System.out.println(
                "Status: " + result.getStatus()
        );
        System.out.println(
                "Message: " + result.getMessage()
        );

        transactionService.updateTransactionStatus(
                result.getTransactionId(),
                result.getStatus()
        );

        System.out.println(
                "Transaction status updated in database"
        );
        System.out.println("========================================");
    }
}