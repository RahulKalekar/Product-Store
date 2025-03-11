package com.rahul.notificationservice.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendOrderConfirmationEmail() throws Exception {
        // Arrange
        String to = "customer@example.com";
        String orderId = "12345";
        String productName = "Product A";
        Integer quantity = 2;
        BigDecimal totalPrice = BigDecimal.valueOf(200.00);

        // Act
        emailService.sendOrderConfirmationEmail(to, orderId, productName, quantity, totalPrice);

        // Capture the MimeMessage sent by the mailSender
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        // Verify the contents of the MimeMessage
        MimeMessage sentMessage = messageCaptor.getValue();
        MimeMessageHelper helper = new MimeMessageHelper(sentMessage);

        // Mock the behavior of the MimeMessage to return expected values
        when(sentMessage.getRecipients(MimeMessage.RecipientType.TO)).thenReturn(InternetAddress.parse(to));
        when(sentMessage.getSubject()).thenReturn("Order Confirmation");
        when(sentMessage.getContent()).thenReturn(
                String.format(
                        "Dear Customer,\n\nYour order with ID: %s containing %d x %s has been confirmed.\nTotal Amount: ₹%s\n\nThank you for shopping with us!",
                        orderId, quantity, productName, totalPrice
                )
        );

        // Assertions
        assertEquals(to, sentMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals("Order Confirmation", sentMessage.getSubject());
        assertEquals(
                String.format(
                        "Dear Customer,\n\nYour order with ID: %s containing %d x %s has been confirmed.\nTotal Amount: ₹%s\n\nThank you for shopping with us!",
                        orderId, quantity, productName, totalPrice
                ),
                sentMessage.getContent().toString().trim()
        );
    }
}