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
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Component
public class EmailManagerImpl implements EmailManager {
    @Value("${SENDER_EMAIL}")
    private String email;

    @Value("${PASSWORD_EMAIL}")
    private String password;

    public void sendSSLEmail(String toEmail, String subject, File body) {
        log.info("SSLEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        log.info("Session created");
        sendEmail(session, toEmail, subject, body);
    }

    public void sendEmail(Session session, String toEmail, String subject, File body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(email, "NoReply-PocketBudget"));

            msg.setReplyTo(InternetAddress.parse(email, false));

            msg.setSubject(subject, "UTF-8");

            String htmlContent = "";
            try (FileInputStream fis = new FileInputStream(body)) {
                htmlContent = new String(fis.readAllBytes(), StandardCharsets.UTF_8);
                htmlContent = htmlContent.replaceAll("%email%", toEmail);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
            msg.setContent(htmlContent, "text/html");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            log.info("Message is ready");
            Transport.send(msg);

            log.info("Email Sent Successfully!!");
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
