package com.fincore.walletservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fincore.walletservice.event.WalletTransactionResult;

@Service
public class WalletTransactionResultProducer {

    private static final String TOPIC = "transaction-results";

    private final KafkaTemplate<String, WalletTransactionResult> kafkaTemplate;

    public WalletTransactionResultProducer(
            KafkaTemplate<String, WalletTransactionResult> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionResult(
            WalletTransactionResult result) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(result.getTransactionId()),
                result
        );
    }
}