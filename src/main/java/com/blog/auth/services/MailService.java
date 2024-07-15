package com.blog.auth.services;

import com.blog.auth.entities.MailBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username}")
    private String from;

    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(mailBody.to());
        simpleMailMessage.setSubject(mailBody.subject());
        simpleMailMessage.setText(mailBody.text());

        javaMailSender.send(simpleMailMessage);
    }
}
