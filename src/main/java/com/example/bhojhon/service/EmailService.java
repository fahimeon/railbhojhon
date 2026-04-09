package com.example.bhojhon.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service to handle email operations.
 */
public class EmailService {

    // Gmail SMTP Configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // REPLACE THESE WITH YOUR ACTUAL GMAIL AND APP PASSWORD
    private static final String SMTP_AUTH_USER = "j84531918@gmail.com";
    private static final String SMTP_AUTH_PWD = "mfovfdzicaskccdw";

    /**
     * Sends an order confirmation email.
     *
     * @param toEmail      Recipient email address
     * @param orderId      The order ID
     * @param orderDetails Formatted order details string
     */
    public void sendOrderConfirmation(String toEmail, String orderId, String orderDetails) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Skipping email: No recipient email provided.");
            return;
        }

        // Setup properties for Gmail SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(SMTP_AUTH_USER, "BhojonOnRails"));

            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set Subject: header field
            message.setSubject("Order Confirmed! #" + orderId + " - BhojonOnRails");

            // Build Email Content
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Dear Customer,\n\n");
            emailBody.append("Thank you for your order! We've received it and are preparing it for you.\n\n");
            emailBody.append("--- ORDER SUMMARY ---\n");
            emailBody.append(orderDetails);
            emailBody.append("\n\nWe will deliver your food to your seat at the specified station.");
            emailBody.append("\n\nEnjoy your meal!\nBhojonOnRails Team");

            // Set the actual message
            message.setText(emailBody.toString());

            // Send message
            Transport.send(message);

            System.out.println("Confirmation email sent successfully to: " + toEmail);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
