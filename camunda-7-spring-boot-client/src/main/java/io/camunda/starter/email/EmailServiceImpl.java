package io.camunda.starter.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Profile("email")
@Service
public class EmailServiceImpl implements EmailService {

    JavaMailSender emailSender;

    @Autowired
    EmailServiceImpl(JavaMailSender emailSender){
        this.emailSender = emailSender;
    }

    @Override
    public void sendMessage(String to, String from, String subject, String cc, String bcc, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        if((to == null || to.length() <= 0) && (cc == null || cc.length() <= 0) && (bcc == null || bcc.length() <= 0)) {
            throw new IllegalStateException("At least one address field (`to`, `cc`, or `bcc`) required to send email");
        }

        if(to != null) {
            message.setTo(to);
        }

        message.setFrom(from == null ? "no-reply@camunda-demo.io" : from);

        message.setSubject(subject == null ? "Camunda Demo Starter" : subject);

        message.setText(text == null ? "" : text);

        if(cc != null) {
            message.setCc(cc);
        }

        if(bcc != null) {
            message.setBcc();
        }

        emailSender.send(message);



    }

}
