package com.fincore.transactionservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fincore.transactionservice.event.TransactionEvent;

@Service
public class TransactionKafkaProducer {

    private static final String TOPIC = "transaction-events";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionKafkaProducer(
            KafkaTemplate<String, TransactionEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(TransactionEvent event) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getTransactionId()),
                event
        );
    }
}