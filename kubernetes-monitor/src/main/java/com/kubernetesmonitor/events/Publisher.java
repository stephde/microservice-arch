package com.kubernetesmonitor.events;

import com.dm.events.Event;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

    @Autowired QueueConfig queueConfig;

    @Autowired
    JmsTemplate jmsTemplate;

    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(queueConfig.BROKER_URL);
        connectionFactory.setPassword(queueConfig.BROKER_USERNAME);
        connectionFactory.setUserName(queueConfig.BROKER_PASSWORD);
        return connectionFactory;
    }

    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(true);
        return template;
    }

    public void publishEvent(Event event) {
        jmsTemplate.convertAndSend(queueConfig.QUEUE_NAME, event, m -> {
            m.setStringProperty("_eventType", event.getType().name());
            return m;
        });
    }
}
