package com.rahul.notificationservice.service;

import com.rahul.common.NotificationDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "order-notifications", groupId = "notification_group")
    public void consume(NotificationDto notificationDto) {
        System.out.println("Received Order: " + notificationDto.getOrderId());

        emailService.sendOrderConfirmationEmail(
                notificationDto.getEmailId(),
                notificationDto.getOrderId(),
                notificationDto.getProductName(),
                notificationDto.getQuantity(),
                notificationDto.getTotalPrice()
        );
    }

}
