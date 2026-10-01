package com.fincore.paymentservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fincore.paymentservice.entity.Payment;
import com.fincore.paymentservice.event.PaymentEvent;
import com.fincore.paymentservice.kafka.PaymentKafkaProducer;
import com.fincore.paymentservice.repository.PaymentRepository;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentKafkaProducer paymentKafkaProducer;

	public PaymentService(PaymentRepository paymentRepository, PaymentKafkaProducer paymentKafkaProducer) {

		this.paymentRepository = paymentRepository;
		this.paymentKafkaProducer = paymentKafkaProducer;
	}

	public Payment createPayment(Long transactionId, Long userId, Long walletId, BigDecimal amount, String currency,
			String paymentMethod) {

		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RuntimeException("Amount must be greater than zero");
		}

		Payment payment = new Payment(transactionId, userId, walletId, amount, currency, paymentMethod, "PENDING",
				LocalDateTime.now());

		Payment savedPayment = paymentRepository.save(payment);

		PaymentEvent event = new PaymentEvent(savedPayment.getId(), savedPayment.getTransactionId(),
				savedPayment.getUserId(), savedPayment.getWalletId(), savedPayment.getAmount(),
				savedPayment.getCurrency(), savedPayment.getPaymentMethod(), savedPayment.getStatus());

		paymentKafkaProducer.sendPaymentEvent(event);

		return savedPayment;
	}

	public Payment getPayment(Long id) {

		return paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
	}

	public List<Payment> getUserPayments(Long userId) {

		return paymentRepository.findByUserId(userId);
	}

	public List<Payment> getTransactionPayments(Long transactionId) {

		return paymentRepository.findByTransactionId(transactionId);
	}

	public Payment updatePaymentStatus(Long paymentId, String status) {

		Payment payment = getPayment(paymentId);

		payment.setStatus(status);

		return paymentRepository.save(payment);
	}
}