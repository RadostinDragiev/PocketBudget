package com.pocketbudget.util.impl;

import com.pocketbudget.util.EmailManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import static com.pocketbudget.constant.EmailConstants.*;

@Slf4j
@Component
public class EmailManagerImpl implements EmailManager {
    @Value("${SENDER_EMAIL}")
    private String email;

    @Value("${PASSWORD_EMAIL}")
    private String password;

    public void sendSSLEmail(String toEmail, String subject, File body) {
        log.info("SSLEmail Start");
        Properties properties = loadProperties();

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        Session session = Session.getDefaultInstance(properties, auth);
        log.info("Session created");
        sendEmail(session, toEmail, subject, body);
    }

    public void sendEmail(Session session, String toEmail, String subject, File body) {
        try {
            MimeMessage message = new MimeMessage(session);
            addHeaders(message);

            message.setFrom(new InternetAddress(email, EMAIL_SENDER_NAME));

            message.setReplyTo(InternetAddress.parse(email, false));

            message.setSubject(subject, StandardCharsets.UTF_8.toString());

            message.setContent(buildBody(body, toEmail), CONTENT_TYPE_HTML);

            message.setSentDate(new Date());

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            log.info("Message is ready");
            Transport.send(message);

            log.info("Email Sent Successfully!!");
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        properties.put(SMTP_HOST, SMTP_HOST_VALUE);
        properties.put(SSL_PORT, SSL_PORT_VALUE);
        properties.put(SSL_FACTORY_CLASS, SSL_FACTORY_CLASS_VALUE);
        properties.put(SMTP_AUTHENTICATION, SMTP_AUTHENTICATION_VALUE);
        properties.put(SMTP_PORT, SMTP_PORT_VALUE);
        return properties;
    }

    /**
     * Add headers to message
     *
     * @param message - message expecting headers
     * @throws MessagingException - exception is handled by sendEmail method
     */
    private void addHeaders(MimeMessage message) throws MessagingException {
        message.addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        message.addHeader(FORMAT, FORMAT_VALUE);
        message.addHeader(CONTENT_TRANSFER_ENCODING, CONTENT_TRANSFER_ENCODING_VALUE);
    }

    /**
     * Construct body of the email
     *
     * @param body    - HTML page added to the message
     * @param toEmail - to be replaced on placeholder
     * @return - ready for email String
     */
    private String buildBody(File body, String toEmail) {
        String htmlContent = "";
        try (FileInputStream fis = new FileInputStream(body)) {
            htmlContent = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
            htmlContent = htmlContent.replaceAll("%email%", toEmail);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return htmlContent;
    }
}
