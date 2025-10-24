package com.rentmate.service.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmate.service.payment.config.RabbitMQConfig;
import com.rentmate.service.payment.controller.PaymentController;
import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.entity.UserEntity;
import com.rentmate.service.payment.refund.RefundData;
import com.rentmate.service.payment.service.PaymentService;
import com.rentmate.service.payment.service.UserService;
import com.rentmate.service.payment.controller.PaymentController;
import com.rentmate.service.payment.status.PaymentStatus;
import com.rentmate.service.payment.status.Status;
import com.rentmate.service.payment.dto.PaymentRequestDTO;
import com.rentmate.service.payment.dto.PaymentResponseDTO;
import com.rentmate.service.payment.user.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageListener {

private final ObjectMapper objectMapper;
@Autowired
private PaymentController paymentController;
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
            case "rental.approved" -> {
                PaymentData paymentdata = objectMapper.convertValue(request.getData(), PaymentData.class);
                Status status = new Status();
                if (paymentdata.getRentalId() == null){
                    status.setrentalId(null);
                    status.setEventType("payment.failed");
                    response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.RENTAL_EXCHANGE,
                            RabbitMQConfig.PAYMENT_ROUTING_KEY_FAILED,
                            objectMapper.writeValueAsString(response)
                    );
                    break;
                }
                paymentdata.setPaymentData(userService);
                paymentdata = paymentService.createPaymentIntent(paymentdata);
                status.setrentalId(paymentdata.getRentalId());
                if (paymentdata.getErrorMessage() != null){
                    status.setEventType("payment.failed");
                    response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.RENTAL_EXCHANGE,
                            RabbitMQConfig.PAYMENT_ROUTING_KEY_FAILED,
                            objectMapper.writeValueAsString(response)
                    );
                }else {
                    response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.RENTAL_EXCHANGE,
                            RabbitMQConfig.PAYMENT_ROUTING_KEY_PAID,
                            objectMapper.writeValueAsString(response)
                    );
                    status.setEventType("payment.paid");
                }

            }
            case "renter.refund" -> {
                RefundData refundData = objectMapper.convertValue(request.getData(), RefundData.class);
                refundData = paymentService.refund(refundData);
                Status status = new Status(refundData.getRentalId(), "payment.refunded");
                response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.RENTAL_EXCHANGE,
                        RabbitMQConfig.PAYMENT_RETURN_ROUTING_KEY,
                        objectMapper.writeValueAsString(response)
                );
            }
            default -> {
                response = new PaymentResponseDTO(null, null);
            }
        }


    } catch (Exception e) {
        PaymentResponseDTO errorResponse =
                new PaymentResponseDTO(null, "payment.failed");
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

