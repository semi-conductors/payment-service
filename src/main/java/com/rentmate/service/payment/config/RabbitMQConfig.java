package com.rentmate.service.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String ExchangeName = "payment.exchange";
    public static final String QueueName = "payment.queue";
    public static final String RoutingKey = "payment.routingkey";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(ExchangeName);
    }

    @Bean
    public Queue queue() {
        return new Queue(QueueName, true);
    }

    @Bean
    public Binding binding(DirectExchange exchange, Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange).
                with(RabbitMQConfig.RoutingKey);
    }
}
