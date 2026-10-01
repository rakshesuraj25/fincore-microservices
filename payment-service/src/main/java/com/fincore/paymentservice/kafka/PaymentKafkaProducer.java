package com.fincore.paymentservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fincore.paymentservice.event.PaymentEvent;

@Service
public class PaymentKafkaProducer {

    private static final String TOPIC = "payment-events";

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentKafkaProducer(
            KafkaTemplate<String, PaymentEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentEvent(PaymentEvent event) {

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(event.getPaymentId()),
                event
        );

        System.out.println(
                "Payment event sent to Kafka: "
                + event.getPaymentId()
        );
    }
}