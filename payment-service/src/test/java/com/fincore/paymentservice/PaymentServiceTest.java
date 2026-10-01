package com.fincore.paymentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fincore.paymentservice.entity.Payment;
import com.fincore.paymentservice.kafka.PaymentKafkaProducer;
import com.fincore.paymentservice.repository.PaymentRepository;
import com.fincore.paymentservice.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentKafkaProducer paymentKafkaProducer;

	@InjectMocks
	private PaymentService paymentService;

	private Payment payment;

	@BeforeEach
	void setUp() {

		payment = new Payment(42L, 2L, 2L, new BigDecimal("500"), "USD", "CARD", "PENDING", LocalDateTime.now());
	}

	@Test
	void shouldCreatePayment() {

		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		Payment result = paymentService.createPayment(42L, 2L, 2L, new BigDecimal("500"), "USD", "CARD");

		assertNotNull(result);
		assertEquals(42L, result.getTransactionId());
		assertEquals(2L, result.getUserId());
		assertEquals(2L, result.getWalletId());
		assertEquals(new BigDecimal("500"), result.getAmount());
		assertEquals("USD", result.getCurrency());
		assertEquals("CARD", result.getPaymentMethod());
		assertEquals("PENDING", result.getStatus());

		verify(paymentRepository).save(any(Payment.class));

		verify(paymentKafkaProducer).sendPaymentEvent(any());
	}

	@Test
	void shouldThrowExceptionForInvalidAmount() {

		assertThrows(RuntimeException.class,
				() -> paymentService.createPayment(42L, 2L, 2L, BigDecimal.ZERO, "USD", "CARD"));
	}

	@Test
	void shouldThrowExceptionForNullAmount() {

		assertThrows(RuntimeException.class, () -> paymentService.createPayment(42L, 2L, 2L, null, "USD", "CARD"));
	}

	@Test
	void shouldGetPayment() {

		when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

		Payment result = paymentService.getPayment(1L);

		assertNotNull(result);
		assertEquals(42L, result.getTransactionId());
		assertEquals(2L, result.getUserId());
		assertEquals(new BigDecimal("500"), result.getAmount());

		verify(paymentRepository).findById(1L);
	}

	@Test
	void shouldThrowExceptionWhenPaymentNotFound() {

		when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> paymentService.getPayment(999L));
	}

	@Test
	void shouldGetUserPayments() {

		Payment payment2 = new Payment(43L, 2L, 2L, new BigDecimal("300"), "USD", "UPI", "SUCCESS",
				LocalDateTime.now());

		when(paymentRepository.findByUserId(2L)).thenReturn(Arrays.asList(payment, payment2));

		java.util.List<Payment> result = paymentService.getUserPayments(2L);

		assertNotNull(result);
		assertEquals(2, result.size());

		verify(paymentRepository).findByUserId(2L);
	}

	@Test
	void shouldGetTransactionPayments() {

		when(paymentRepository.findByTransactionId(42L)).thenReturn(Arrays.asList(payment));

		java.util.List<Payment> result = paymentService.getTransactionPayments(42L);

		assertNotNull(result);
		assertEquals(1, result.size());

		verify(paymentRepository).findByTransactionId(42L);
	}

	@Test
	void shouldUpdatePaymentStatus() {

		when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

		when(paymentRepository.save(payment)).thenReturn(payment);

		Payment result = paymentService.updatePaymentStatus(1L, "SUCCESS");

		assertEquals("SUCCESS", result.getStatus());

		verify(paymentRepository).findById(1L);

		verify(paymentRepository).save(payment);
	}
}