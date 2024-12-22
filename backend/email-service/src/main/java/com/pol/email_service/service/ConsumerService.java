package com.pol.email_service.service;


import com.pol.payment_service.schema.avro.PaymentSuccessfulEvent;
import com.pol.user_service.schema.avro.ForgotPasswordEvent;
import com.pol.user_service.schema.avro.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final ResendService resendService;

    public ConsumerService(ResendService resendService) {
        this.resendService = resendService;
    }

    @KafkaListener(topics = "user-registered-topic", groupId = "notification-service")
    public void processRegistrationOtp(UserRegisteredEvent event, Acknowledgment acknowledgment) {
        try {
            resendService.sendWelcomeEmail(event.getEmail().toString(),event.getName().toString());
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            System.err.println("Error processing event: " + ex.getMessage());
        }
    }

    @KafkaListener(topics = "forgot-password-topic", groupId = "notification-service")
    public void processForgotPasswordOtp(ForgotPasswordEvent event, Acknowledgment acknowledgment) {
        try {
            resendService.sendForgotPasswordOTPEmail(event.getEmail().toString(),event.getName().toString(),event.getOtp().toString());
            acknowledgment.acknowledge();
        } catch(Exception ex) {
            System.err.println("Error processing event: " + ex.getMessage());
        }
    }

    @KafkaListener(topics = "payment-successful-topic", groupId = "notification-service")
    public void processSuccessfulPayment(PaymentSuccessfulEvent event, Acknowledgment acknowledgment) {
        try {
            resendService.sendPaymentSuccesfulEmail(event.getEmail().toString(),event.getUsername().toString(),event.getSub().toString(),event.getPaymentId().toString(), event.getProductId().toString(), event.getOrderId().toString());
            acknowledgment.acknowledge();
        } catch(Exception ex) {
            System.err.println("Error processing event: " + ex.getMessage());
        }
    }
}
