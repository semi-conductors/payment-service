package com.rentmate.service.payment.service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeMessage(String message) {
    }
}
