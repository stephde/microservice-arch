package com.modelgenerator;

import com.dm.events.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Controller {

    @Autowired ModelWrapper modelWrapper;
    @Autowired JmsTemplate jmsTemplate;

    @GetMapping("/model")
    public String load() {
        return this.modelWrapper.getModelAsJSON().toString();
    }

    @PostMapping("/message")
    public void sendMessage(@RequestBody String message) {
        log.info("Sending message: {}", message);
        DeploymentEvent event = new DeploymentEvent("User Management Service #1", DateTime.now(), DateTime.now());
        jmsTemplate.convertAndSend("model-updates", message, m -> {
            m.setStringProperty("_eventType", event.getType().name());
            return m;
        });
    }
}
