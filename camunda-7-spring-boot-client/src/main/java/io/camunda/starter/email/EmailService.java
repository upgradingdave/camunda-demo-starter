package io.camunda.starter.email;

public interface EmailService {

    public void sendMessage(String to, String from, String subject, String cc, String bcc, String text);

}
