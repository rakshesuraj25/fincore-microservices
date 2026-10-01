package com.fincore.walletservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fincore.walletservice.entity.Wallet;
import com.fincore.walletservice.event.TransactionEvent;
import com.fincore.walletservice.event.WalletTransactionResult;
import com.fincore.walletservice.service.WalletService;

@Service
public class TransactionKafkaConsumer {

    private final WalletService walletService;
    private final WalletTransactionResultProducer resultProducer;

    public TransactionKafkaConsumer(
            WalletService walletService,
            WalletTransactionResultProducer resultProducer) {

        this.walletService = walletService;
        this.resultProducer = resultProducer;
    }

    @KafkaListener(
            topics = "transaction-events",
            groupId = "fincore-wallet-group"
    )
    public void consumeTransactionEvent(TransactionEvent event) {

        System.out.println("========================================");
        System.out.println("Kafka Transaction Event Received");
        System.out.println("Transaction ID: " + event.getTransactionId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Wallet ID: " + event.getWalletId());
        System.out.println("Type: " + event.getType());
        System.out.println("Amount: " + event.getAmount());
        System.out.println("Currency: " + event.getCurrency());
        System.out.println("Status: " + event.getStatus());

        /*
         * Only process PENDING transactions.
         */
        if (!"PENDING".equalsIgnoreCase(event.getStatus())) {

            System.out.println(
                    "Transaction is not PENDING. Skipping wallet update."
            );

            System.out.println("========================================");

            return;
        }

        try {

            Wallet wallet;

            if ("DEPOSIT".equalsIgnoreCase(event.getType())) {

                wallet = walletService.addMoney(
                        event.getWalletId(),
                        event.getAmount()
                );

                System.out.println(
                        "Wallet balance updated: DEPOSIT"
                );

            } else if ("WITHDRAW".equalsIgnoreCase(event.getType())) {

                wallet = walletService.withdrawMoney(
                        event.getWalletId(),
                        event.getAmount()
                );

                System.out.println(
                        "Wallet balance updated: WITHDRAW"
                );

            } else {

                throw new RuntimeException(
                        "Unknown transaction type: "
                                + event.getType()
                );
            }

            WalletTransactionResult result =
                    new WalletTransactionResult(
                            event.getTransactionId(),
                            "SUCCESS",
                            "Wallet updated successfully"
                    );

            resultProducer.sendTransactionResult(result);

            System.out.println(
                    "Transaction result sent: SUCCESS"
            );

        } catch (Exception e) {

            System.out.println(
                    "Wallet update failed: " + e.getMessage()
            );

            WalletTransactionResult result =
                    new WalletTransactionResult(
                            event.getTransactionId(),
                            "FAILED",
                            e.getMessage()
                    );

            resultProducer.sendTransactionResult(result);

            System.out.println(
                    "Transaction result sent: FAILED"
            );
        }

        System.out.println("========================================");
    }
}