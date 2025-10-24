package com.rentmate.service.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMQConfig {
    public static final String REQUEST_QUEUE = "payment.request.queue";
    public static final String RESPONSE_QUEUE = "payment.response.queue";
    public static final String EXCHANGE = "payment.exchange";
    public static final String REQUEST_ROUTING_KEY = "payment.request";
    public static final String RESPONSE_ROUTING_KEY = "payment.response";

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(REQUEST_QUEUE, true);
    }

    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding requestBinding(@Qualifier("paymentRequestQueue") Queue paymentRequestQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentExchange).with(REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding responseBinding(@Qualifier("paymentResponseQueue") Queue paymentResponseQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentResponseQueue).to(paymentExchange).with(RESPONSE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
