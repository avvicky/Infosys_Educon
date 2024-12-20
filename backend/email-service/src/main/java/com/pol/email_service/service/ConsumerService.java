package com.pol.email_service.service;


import com.pol.user_service.schema.avro.ForgotPasswordEvent;
import com.pol.user_service.schema.avro.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private final ResendService resendService;

    public ConsumerService(ResendService resendService) {
        this.resendService = resendService;
    }

    @KafkaListener(topics = "user-registered-topic", groupId = "notification-service")
    public void processRegistrationOtp(UserRegisteredEvent event) {
        resendService.sendWelcomeEmail(event.getEmail().toString(),event.getName().toString());
    }

    @KafkaListener(topics = "forgot-password-topic", groupId = "notification-service")
    public void processForgotPasswordOtp(ForgotPasswordEvent event) {
        resendService.sendForgotPasswordOTPEmail(event.getEmail().toString(),event.getName().toString(),event.getOtp().toString());
    }
}
