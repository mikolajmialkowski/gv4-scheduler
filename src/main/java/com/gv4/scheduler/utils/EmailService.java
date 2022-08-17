package com.gv4.scheduler.utils;

import com.gv4.scheduler.configuration.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
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

    private static final String HTML_OPEN_H2 = "<h2>";
    private static final String HTML_CLOSE_H2 = "</h2>";
    private static final String HTML_OPEN_BOLD = "<b>";
    private static final String HTML_CLOSE_BOLD = "</b>";
    private static final String HTML_BREAK_LINE = "<br>";

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

    public void sendErrorMessage(JobExecution jobExecution ,List<Exception> exceptionList){

        LOGGER.warn("Sending error message to DEV(s)");
        this.simpleMailMessage.setFrom(emailProperties.getUsername());
        this.simpleMailMessage.setTo(this.emailProperties.getDefaultsRecipients());
        this.simpleMailMessage.setSubject("GV4 - Batch Processing - unexpected error occurred");

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(HTML_OPEN_H2)
                .append("Unfortunately, during job execution with name: ")
                .append(jobExecution.getJobConfigurationName())
                .append(", unexpected error occurred")
                .append(HTML_CLOSE_H2)
                .append(HTML_BREAK_LINE)
                .append("Job Execution ID: ")
                .append(HTML_OPEN_BOLD)
                .append(jobExecution.getJobId())
                .append(HTML_CLOSE_BOLD)
                .append(HTML_BREAK_LINE)
                .append(HTML_BREAK_LINE)
                .append("Program encountered the following unhandled exception: ")
                .append(HTML_BREAK_LINE)
                .append(HTML_OPEN_BOLD)
                .append(jobExecution.getAllFailureExceptions().toString())
                .append(HTML_CLOSE_BOLD)
                .append(HTML_BREAK_LINE)
                .append(HTML_BREAK_LINE)
                .append("Job trigger timestamp: ")
                .append(HTML_OPEN_BOLD)
                .append(jobExecution.getJobParameters().toString())
                .append(HTML_CLOSE_BOLD)
                .append(HTML_BREAK_LINE)
                .append(HTML_BREAK_LINE)
                .append("For more information check out execution logs.")
                .append(HTML_BREAK_LINE)
                .append(HTML_BREAK_LINE)
                .append("This email was generated automatically, please don't reply to it.")
                .append(LocalDateTime.now());

        this.simpleMailMessage.setText(errorMessage.toString());
        this.javaMailSender.send(this.simpleMailMessage);
    }
}
