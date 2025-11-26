package com.rentmate.service.payment.service;
import com.rentmate.service.payment.config.RabbitMQConfig;
import com.rentmate.service.payment.dto.NotificationTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class Notifiaction {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public Notifiaction(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendPaidNotification(Long amount, Long userId){
          NotificationTemplate notificationTemplate = new NotificationTemplate();
          notificationTemplate.setUserId(userId);
          notificationTemplate.setEventType("payment-success");
          notificationTemplate.addParam("amount", amount);
          notificationTemplate.addParam("date", LocalDate.now().toString());
          rabbitTemplate.convertAndSend(
                  RabbitMQConfig.NOTIFICATION_EXCHANGE,
                  RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                  notificationTemplate
          );
      }

      public void sendRefundNotification(Long amount, Long userId){
        NotificationTemplate notificationTemplate = new NotificationTemplate();
        notificationTemplate.setUserId(userId);
        notificationTemplate.setEventType("payment-refund");
        notificationTemplate.addParam("amount", amount);
        notificationTemplate.addParam("date", LocalDate.now().toString());
          rabbitTemplate.convertAndSend(
                  RabbitMQConfig.NOTIFICATION_EXCHANGE,
                  RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                  notificationTemplate
          );
      }
}
