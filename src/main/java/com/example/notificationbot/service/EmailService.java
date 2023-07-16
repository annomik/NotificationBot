package com.example.notificationbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void send(String mailRecipient, String subject, String message) {
        SimpleMailMessage sendingMessage = new SimpleMailMessage();
        sendingMessage.setFrom(username);
        sendingMessage.setTo(mailRecipient);
        sendingMessage.setSubject(subject);
        sendingMessage.setText(message);

        mailSender.send(sendingMessage);
    }

}
