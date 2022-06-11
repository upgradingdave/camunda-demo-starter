package io.camunda.starter.workers;

import io.camunda.starter.email.EmailService;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

@Component
public class EmailWorker {

    private final Logger LOGGER = Logger.getLogger(EmailWorker.class.getName());

    // http://localhost:8080/engine-rest/
    private final String restApiUrl;

    private final EmailService emailService;

    ExternalTaskClient client;
    TopicSubscriptionBuilder topicSubscriptionBuilder;

    public EmailWorker(@Value("${camunda.bpm.restApiUrl}") String restApiUrl,
                       EmailService emailService
    ) {
        this.restApiUrl = restApiUrl;
        this.emailService = emailService;
        init();
    }

    public void init() {
        LOGGER.info("\n\n  "+EmailWorker.class.getName()+" initializing \n\n");
        createClient();
        startClient();
    }

    public void createClient() {
        client = ExternalTaskClient.create()
                .baseUrl(restApiUrl)
                .asyncResponseTimeout(10000)
                .disableBackoffStrategy()
                .disableAutoFetching()
                .maxTasks(1)
                .build();

        topicSubscriptionBuilder = client.subscribe("send-email")
                .lockDuration(20000)
                .handler((externalTask, externalTaskService) -> {
                    String from = externalTask.getVariable("emailFrom");
                    String to = externalTask.getVariable("emailTo");
                    String cc = externalTask.getVariable("emailCc");
                    String bcc = externalTask.getVariable("emailBcc");
                    String subject = externalTask.getVariable("emailSubject");
                    String text = externalTask.getVariable("emailText");

                    try {

                        emailService.sendMessage(to, from, subject, cc, bcc, text);

                        LOGGER.info("\n\n  "+EmailWorker.class.getName()+" sent email message"
                                + "\n emailFrom: " + from
                                + "\n emailTo: " + to
                                + "\n emailCc: " + cc
                                + "\n emailBcc: " + bcc
                                + "\n emailSubject: " + subject
                                + "\n emailText: " + text
                                +"\n\n");


                        externalTaskService.complete(externalTask);

                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String stackTrace = sw.toString();
                        externalTaskService.handleFailure(externalTask.getId(), externalTask.getWorkerId(), stackTrace, 0, 0L);
                    }
                });
    }

    public void startClient() {
        client.start();
        topicSubscriptionBuilder.open();
    }

    public void stopClient (){
        client.stop();
    }

}
