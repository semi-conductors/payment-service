package com.rentmate.service.payment.service;

import com.rentmate.service.payment.config.RabbitMQConfig;
import com.stripe.model.InvoicePayment;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendPayment(String message) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.ExchangeName,
                RabbitMQConfig.RoutingKey,
                message
        );
    }
}
