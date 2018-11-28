package com.modelgenerator.event;

import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.ServiceEvent;
import com.modelgenerator.ModelWrapper;
import de.mdelab.comparch.ComponentState;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    @Autowired
    QueueConfig queueConfig;

    @Autowired
    ModelWrapper modelWrapper;

    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(queueConfig.BROKER_URL);
        connectionFactory.setPassword(queueConfig.BROKER_USERNAME);
        connectionFactory.setUserName(queueConfig.BROKER_PASSWORD);
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
        return converter;
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'DEPLOYMENT_STATUS_UPDATE'")
    public void receiveEvent(@Payload final DeploymentEvent event) {
        System.out.println("Received Event object : " + event.toString());

        ComponentState state = parseState(event.getStatus());
        try {
            modelWrapper.handleStateUpdate(event.getServiceName(), event.getComponentName(), state);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'SERVICE_UPDATE'")
    public void receiveEvent(@Payload final ServiceEvent event) {
        System.out.println("Received Event object : " + event.toString());
    }

    private ComponentState parseState(String remoteState) {
        switch (remoteState) {
            case "STARTED": return ComponentState.DEPLOYED;
            case "RUNNING": return ComponentState.STARTED;
            case "REMOVED": return ComponentState.UNDEPLOYED;
            default: return ComponentState.UNDEPLOYED;
        }
    }
}
