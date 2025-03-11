package com.rahul.notificationservice.service;

import com.rahul.common.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto();
        notificationDto.setEmailId("customer@example.com");
        notificationDto.setOrderId("12345");
        notificationDto.setProductName("Product A");
        notificationDto.setQuantity(2);
        notificationDto.setTotalPrice(BigDecimal.valueOf(200.00));
    }

    @Test
    void consume() {
        // Act
        notificationService.consume(notificationDto);

        // Capture the arguments passed to the emailService
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> orderIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> productNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> quantityCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<BigDecimal> totalPriceCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        // Verify that the emailService was called with the correct arguments
        verify(emailService).sendOrderConfirmationEmail(
                emailCaptor.capture(),
                orderIdCaptor.capture(),
                productNameCaptor.capture(),
                quantityCaptor.capture(),
                totalPriceCaptor.capture()
        );

        // Assert that the captured arguments match the expected values
        assertEquals("customer@example.com", emailCaptor.getValue());
        assertEquals("12345", orderIdCaptor.getValue());
        assertEquals("Product A", productNameCaptor.getValue());
        assertEquals(2, quantityCaptor.getValue());
        assertEquals(BigDecimal.valueOf(200.00), totalPriceCaptor.getValue());
    }
}