package com.rentmate.service.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentmate.service.payment.config.RabbitMQConfig;
import com.rentmate.service.payment.controller.PaymentController;
import com.rentmate.service.payment.data.PaymentData;
import com.rentmate.service.payment.dto.MessageRequest;
import com.rentmate.service.payment.refund.RefundData;
import com.rentmate.service.payment.status.Status;
import com.rentmate.service.payment.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rentmate.service.payment.service.Notifiaction;

@Component
@Slf4j
public class PaymentMessageListener {

private final ObjectMapper objectMapper;
@Autowired
private PaymentController paymentController;
private final PaymentService paymentService;
private final UserService userService;
private final RabbitTemplate rabbitTemplate;
private final Notifiaction notifiaction;

public PaymentMessageListener(ObjectMapper objectMapper,
                              PaymentService paymentService,
                              UserService userService,
                              RabbitTemplate rabbitTemplate,
                              Notifiaction notifiaction) {
    this.objectMapper = objectMapper;
    this.paymentService = paymentService;
    this.userService = userService;
    this.rabbitTemplate = rabbitTemplate;
    this.notifiaction = notifiaction;
}


    @RabbitListener(queues = RabbitMQConfig.REQUEST_QUEUE)
    public void handleRequest(MessageRequest message) {
        log.info("start handling request");
        try {
            switch (message.getEventType()) {
                case "rental.approved" -> {
                    PaymentResponseDTO response;
                    log.info("handling paymentRequest");
                    PaymentData paymentdata = message.getPaymentData();
                    Status status = new Status();
                    if (paymentdata.getRentalId() == null){
                        status.setrentalId(null);
                        status.setEventType("payment.failed");
                        response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());

                        rabbitTemplate.convertAndSend(
                                RabbitMQConfig.RENTAL_EXCHANGE,
                                RabbitMQConfig.PAYMENT_ROUTING_KEY_FAILED,
                                response
                        );
                        log.info("published paid event to rentalService");
                        break;
                    }
                    log.info("before payment");
                    paymentdata.setPaymentData(userService);
                    paymentdata = paymentService.createPaymentIntent(paymentdata);
                    log.info("payment created");
                    status.setrentalId(paymentdata.getRentalId());
                    if (paymentdata.getErrorMessage() != null){
                        status.setEventType("payment.failed");
                        response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                        rabbitTemplate.convertAndSend(
                                RabbitMQConfig.RENTAL_EXCHANGE,
                                RabbitMQConfig.PAYMENT_ROUTING_KEY_FAILED,
                                response
                        );
                    }else {
                        notifiaction.sendPaidNotification(paymentdata.getAmount(), paymentdata.getRenterId());
                    }

                }
                case "rental.refund" -> {
                    log.info("handle refund event");
                    PaymentResponseDTO response;
                    RefundData refundData = message.getRefundData();
                    refundData = paymentService.refund(refundData);
                    Status status = new Status(refundData.getRentalId(), "payment.refunded");
                    response = new PaymentResponseDTO(status.getRentalId(), status.getEventType());
                    notifiaction.sendRefundNotification(refundData.getAmount(), refundData.getRenterId());
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.RENTAL_EXCHANGE,
                            RabbitMQConfig.PAYMENT_RETURN_ROUTING_KEY,
                            response
                    );
                    log.info("publish refund event to rental service");
                }
                default -> {
                    PaymentResponseDTO response = new PaymentResponseDTO(null, null);
                }
            }


        } catch (Exception e) {
            PaymentResponseDTO errorResponse =
                    new PaymentResponseDTO(null, "payment.failed");
            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE,
                        RabbitMQConfig.RESPONSE_ROUTING_KEY,
                       errorResponse
                );
            } catch (Exception ignored) {
                log.info("error###########");
            }
        }
    }
}

