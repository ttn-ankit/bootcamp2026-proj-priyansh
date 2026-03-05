package com.example.ecommerceproject.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.ecommerceproject.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final String from;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    @Async
    public void sendActivationEmail(String toEmail, String token) {
        String activationLink = "http://localhost:8080/api/auth/activate?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Activate your account");
        mailMessage.setText(
                """
                        Welcome to Ecommerce Platform!

                        Please activate your account using the link below:

                        %s

                        This link will expire in 3 hours.

                        If you did not register, ignore this email.
                        """.formatted(activationLink));
        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendSellerRegistrationEmail(String email) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(email);
        mail.setSubject("Seller Registration Received");

        mail.setText(
                """
                        Dear Seller,

                        Your seller account has been successfully created.

                        Our team will review your details and approve your account shortly.

                        You will be notified once the approval process is complete.

                        Regards,
                        Ecommerce Team
                        """);

        mailSender.send(mail);
    }

}
