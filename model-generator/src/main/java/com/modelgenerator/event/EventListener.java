package com.modelgenerator.event;

import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.ServiceEvent;
import com.kubernetesmonitor.events.NamespaceEvent;
import com.modelgenerator.ModelWrapper;
import de.mdelab.comparch.ComponentState;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Received : {}", event.toString());

        ComponentState state = parseState(event.getStatus());
        modelWrapper.handleStateUpdate(event.getServiceName(), event.getComponentName(), event.getNodeName(), state);
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'SERVICE_UPDATE'")
    public void receiveEvent(@Payload final ServiceEvent event) {
        log.info("Received : {} ", event.toString());
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'NAMESPACE_UPDATE'")
    public void receiveEvent(@Payload final NamespaceEvent event) {
        log.info("Received : {} ", event.toString());
        modelWrapper.generateModel(event.getName());
    }

    private ComponentState parseState(String remoteState) {
        switch (remoteState) {
            case "Pending": return ComponentState.STARTED;
            case "Running": return ComponentState.DEPLOYED;
            case "Removed": return ComponentState.UNDEPLOYED;
            default: return ComponentState.UNDEPLOYED;
        }
    }
}
