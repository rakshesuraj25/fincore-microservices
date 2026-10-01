package com.fincore.transactionservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fincore.transactionservice.event.PaymentResult;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, PaymentResult> paymentResultConsumerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getBootstrapServers()
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "fincore-transaction-payment-group"
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        props.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                PaymentResult.class.getName()
        );

        props.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.fincore.transactionservice.event"
        );

        props.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentResult>
            paymentResultKafkaListenerContainerFactory(
                    ConsumerFactory<String, PaymentResult> paymentResultConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, PaymentResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(paymentResultConsumerFactory);

        return factory;
    }
}