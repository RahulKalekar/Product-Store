package com.rahul.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import java.math.BigDecimal;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmationEmail(String to, String orderId, String productName, Integer quantity, BigDecimal totalPrice) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Order Confirmation");

            String emailContent = String.format(
                    "Dear Customer,\n\nYour order with ID: %s containing %d x %s has been confirmed.\nTotal Amount: ₹%s\n\nThank you for shopping with us!",
                    orderId, quantity, productName, totalPrice
            );

            helper.setText(emailContent);

            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
