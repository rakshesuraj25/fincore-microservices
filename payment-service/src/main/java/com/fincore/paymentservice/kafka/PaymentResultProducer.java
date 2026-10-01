package com.fincore.paymentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fincore.paymentservice.event.PaymentResult;

@Service
public class PaymentResultProducer {

    private static final String TOPIC = "payment-results";

    private final KafkaTemplate<String, PaymentResult> kafkaTemplate;

    public PaymentResultProducer(
            KafkaTemplate<String, PaymentResult> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentResult(PaymentResult result) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(result.getPaymentId()),
                result
        );

        System.out.println(
                "Payment result sent: "
                + result.getStatus()
        );
    }
}