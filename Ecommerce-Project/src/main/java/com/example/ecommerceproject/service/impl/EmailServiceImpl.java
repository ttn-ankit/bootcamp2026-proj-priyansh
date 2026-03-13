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

    @Override
    @Async
    public void sendAccountLockedEmail(String email) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(email);
        mail.setSubject("Account Locked");
        mail.setText(
                """
                Your account has been locked due to multiple failed login attempts.

                Please contact support to unlock your account.

                Regards,
                Ecommerce Team
                """);
        mailSender.send(mail);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Reset your password");
        mailMessage.setText(
                """
                        We received a request to reset your password.

                        Use the link below to reset it:

                        %s

                        This link will expire soon. If you did not request this, you can ignore this email.
                        """.formatted(resetLink));
        mailSender.send(mailMessage);
    }

    @Override
    @Async
    public void sendPasswordChangedEmail(String toEmail) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Your password was changed");
        mailMessage.setText(
                """
                        Your password was recently changed.

                        If you did not perform this action, please contact support immediately.

                        Regards,
                        Ecommerce Team
                        """);
        mailSender.send(mailMessage);
    }

}
