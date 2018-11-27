package com.modelgenerator.event;

import com.kubernetesmonitor.events.DeploymentEvent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EventListener {

    private static final String BROKER_URL = "tcp://activemq:61616";
    private static final String BROKER_USERNAME = "admin";
    private static final String BROKER_PASSWORD = "admin";

    private static final String QUEUE_NAME = "model.updates";

    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_USERNAME);
        connectionFactory.setUserName(BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setConcurrency("1-1");
        factory.setPubSubDomain(true);
        return factory;
    }

    // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        HashMap<String, Class<?>> idMapping = new HashMap<String, Class<?>>();
        idMapping.put(DeploymentEvent.class.getName(), DeploymentEvent.class);
        converter.setTypeIdMappings(idMapping);

        return converter;
    }

    @JmsListener(destination = QUEUE_NAME, containerFactory = "jmsListenerContainerFactory")
    public void receiveEvent(@Payload final DeploymentEvent event) {
//        Map map = new Gson().fromJson(messageData, Map.class);
        System.out.println("Received Event object : " + event.toString());
    }

}
