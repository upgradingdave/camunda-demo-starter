package io.camunda.starter.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Profile("test")
@Service
public class EmailServiceMockImpl implements EmailService {

    private final Logger LOGGER = Logger.getLogger(EmailServiceMockImpl.class.getName());

    @Autowired
    EmailServiceMockImpl(){}

    @Override
    public void sendMessage(String to, String from, String subject, String cc, String bcc, String text) {

        LOGGER.info("\n\n  "+ EmailService.class.getName()+" is pretending to send an email."
                + "\n !!! You can send actual emails if you start the smtp component that is part of the Full Demo "
                + "Start Environment. And remember to use the `email` profile. !!!"
                +"\n\n");
    }

}
