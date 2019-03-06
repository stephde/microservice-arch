package com.modelmaintainer;

import com.dm.events.DeploymentEvent;
import com.modelmaintainer.model.JSONParser;
import com.modelmaintainer.model.ModelWrapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = "http://fb14srv7.hpi.uni-potsdam.de:1801", maxAge = 3600)
public class Controller {

    @Autowired
    ModelWrapper modelWrapper;
    @Autowired JmsTemplate jmsTemplate;
    @Autowired
    JSONParser jsonParser;

    @GetMapping("/model")
    public String load() {
        return jsonParser.getModelAsJSON(this.modelWrapper.getModel()).toString();
    }

    @GetMapping("/savemodel")
    public void saveModel() {
        modelWrapper.save();
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
