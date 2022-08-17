package com.gv4.scheduler.utils;

import com.gv4.scheduler.configuration.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final SimpleMailMessage simpleMailMessage;
    private final JavaMailSenderImpl javaMailSender;
    private final EmailProperties emailProperties;

    public EmailService(JavaMailSenderImpl javaMailSender, EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
        this.simpleMailMessage = new SimpleMailMessage();
        this.javaMailSender = javaMailSender;
    }

    public void sendCustomMessage(String subject, String message, List<String> recipients){

        LOGGER.warn("Sending custom message");
        this.simpleMailMessage.setFrom(emailProperties.getUsername());
        this.simpleMailMessage.setTo(recipients.toArray(new String[0]));
        this.simpleMailMessage.setSubject(subject);
        this.simpleMailMessage.setText(message);
        this.javaMailSender.send(simpleMailMessage);
    }

    public void sendErrorMessage(String error){

        LOGGER.warn("Sending error message to DEV(s)");
        this.simpleMailMessage.setFrom(emailProperties.getUsername());
        this.simpleMailMessage.setTo(this.emailProperties.getDefaultsRecipients());
        this.simpleMailMessage.setSubject("GV4 - Batch Processing - unexpected error occurred");

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Timestamp: ")
                .append(LocalDateTime.now())
                .append(System.lineSeparator())
                .append("Program encountered the following unhandled exception: ")
                .append(System.lineSeparator())
                .append(error)
                .append(System.lineSeparator())
                .append("For more information check out execution logs");

        this.simpleMailMessage.setText(errorMessage.toString());
        this.javaMailSender.send(this.simpleMailMessage);
    }
}
