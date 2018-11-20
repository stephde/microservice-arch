package com.modelgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired ModelWrapper modelWrapper;
    @Autowired JmsTemplate jmsTemplate;

    @GetMapping("/model")
    public String load() {
        return this.modelWrapper.getModel().toString();
    }

    @PostMapping("/message")
    public void sendMessage(@RequestBody String message) {
        System.out.println("Sending message: " + message);
        jmsTemplate.convertAndSend("updates", message);
    }
}
