package com.rentmate.service.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmate.service.payment.config.RabbitMQConfig;
import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.refund.RefundData;
import com.rentmate.service.payment.service.PaymentService;
import com.rentmate.service.payment.service.UserService;
import com.rentmate.service.payment.status.PaymentStatus;
import com.rentmate.service.payment.status.Status;
import com.rentmate.service.payment.dto.PaymentRequestDTO;
import com.rentmate.service.payment.dto.PaymentResponseDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageListener {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    public PaymentMessageListener(ObjectMapper objectMapper,
                                  PaymentService paymentService,
                                  UserService userService,
                                  RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE)
    public void handleRequest(String message) {
        try {
            PaymentRequestDTO request = objectMapper.readValue(message, PaymentRequestDTO.class);
            PaymentResponseDTO response;

            switch (request.geteventType().toLowerCase()) {
                case "createpayment" -> {
                    PaymentData data = objectMapper.convertValue(request.getData(), PaymentData.class);
                    Status status;
                    if (data.getErrorMessage() == null){
                        status = new Status(PaymentStatus.SUCCESS);
                    }else {
                        status = new Status(PaymentStatus.FAILED, data.getErrorMessage());
                    }
                    response = new PaymentResponseDTO(status.getStatus(), data.getErrorMessage());
                }
                case "refund" -> {
                    RefundData refund = objectMapper.convertValue(request.getData(), RefundData.class);
                    Status status = new Status(PaymentStatus.SUCCESS);
                    response = new PaymentResponseDTO(status.getStatus(), null);
                }
                default -> {
                    response = new PaymentResponseDTO(null, null);
                }
            }

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.RESPONSE_ROUTING_KEY,
                    objectMapper.writeValueAsString(response)
            );

        } catch (Exception e) {
            PaymentResponseDTO errorResponse =
                    new PaymentResponseDTO("FAILED", null);
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE,
                        RabbitMQConfig.RESPONSE_ROUTING_KEY,
                        objectMapper.writeValueAsString(errorResponse)
                );
            } catch (Exception ignored) {}
        }
    }
}

