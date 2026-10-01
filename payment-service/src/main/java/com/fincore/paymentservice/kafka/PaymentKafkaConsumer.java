package com.fincore.paymentservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fincore.paymentservice.entity.Payment;
import com.fincore.paymentservice.event.PaymentEvent;
import com.fincore.paymentservice.event.PaymentResult;
import com.fincore.paymentservice.service.PaymentService;

@Service
public class PaymentKafkaConsumer {

    private final PaymentService paymentService;
    private final PaymentResultProducer resultProducer;

    public PaymentKafkaConsumer(
            PaymentService paymentService,
            PaymentResultProducer resultProducer) {

        this.paymentService = paymentService;
        this.resultProducer = resultProducer;
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "fincore-payment-group"
    )
    public void consumePaymentEvent(PaymentEvent event) {

        System.out.println("========================================");
        System.out.println("Kafka Payment Event Received");
        System.out.println("Payment ID: " + event.getPaymentId());
        System.out.println("Transaction ID: " + event.getTransactionId());
        System.out.println("User ID: " + event.getUserId());
        System.out.println("Wallet ID: " + event.getWalletId());
        System.out.println("Amount: " + event.getAmount());
        System.out.println("Currency: " + event.getCurrency());
        System.out.println("Payment Method: " + event.getPaymentMethod());
        System.out.println("Status: " + event.getStatus());

        if (!"PENDING".equalsIgnoreCase(event.getStatus())) {

            System.out.println("Payment is not PENDING. Skipping.");
            System.out.println("========================================");
            return;
        }

        try {

            Payment payment =
                    paymentService.getPayment(event.getPaymentId());

            if (!"PENDING".equalsIgnoreCase(payment.getStatus())) {

                System.out.println(
                        "Payment already processed. Current status: "
                        + payment.getStatus()
                );

                System.out.println("========================================");
                return;
            }

            paymentService.updatePaymentStatus(
                    event.getPaymentId(),
                    "SUCCESS"
            );

            System.out.println("Payment processed successfully");
            System.out.println("Payment status updated: SUCCESS");

            PaymentResult result = new PaymentResult(
                    event.getPaymentId(),
                    event.getTransactionId(),
                    "SUCCESS",
                    "Payment processed successfully"
            );

            resultProducer.sendPaymentResult(result);

        } catch (Exception e) {

            System.out.println(
                    "Payment processing failed: " + e.getMessage()
            );

            try {

                paymentService.updatePaymentStatus(
                        event.getPaymentId(),
                        "FAILED"
                );

                System.out.println(
                        "Payment status updated: FAILED"
                );

                PaymentResult result = new PaymentResult(
                        event.getPaymentId(),
                        event.getTransactionId(),
                        "FAILED",
                        e.getMessage()
                );

                resultProducer.sendPaymentResult(result);

            } catch (Exception updateException) {

                System.out.println(
                        "Could not update payment status: "
                        + updateException.getMessage()
                );
            }
        }

        System.out.println("========================================");
    }
}