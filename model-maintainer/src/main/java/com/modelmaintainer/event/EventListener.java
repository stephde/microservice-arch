package com.modelmaintainer.event;

import com.dm.events.DependencyEvent;
import com.dm.events.DeploymentEvent;
import com.dm.events.NamespaceEvent;
import com.dm.events.ServiceEvent;
import com.modelmaintainer.model.ModelWrapper;
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

        modelWrapper.handleInstanceStateUpdate(
                event.getServiceName(),
                event.getComponentName(),
                event.getNodeName(),
                parseState(event.getStatus()),
                event.getEventDateTime(),
                event.getCreationDateTime(),
                event.getRuntimeEnv());
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'SERVICE_UPDATE'")
    public void receiveEvent(@Payload final ServiceEvent event) {
        log.info("Received : {} ", event.toString());
        modelWrapper.handleServiceStateUpdate(
                event.getComponentName(),
                event.getClusterIP(),
                event.getPorts());
    }

    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'NAMESPACE_UPDATE'")
    public void receiveEvent(@Payload final NamespaceEvent event) {
        log.info("Received : {} ", event.toString());
        modelWrapper.generateModel(event.getName());
    }
    @JmsListener(destination = "${spring.activemq.queue-name}",
                containerFactory = "jmsListenerContainerFactory",
                selector = "_eventType = 'DEPENDENCY_UPDATE'")
    public void receiveEvent(@Payload final DependencyEvent event) {
        log.info("Received : {} ", event.toString());
        modelWrapper.handleDependencyUpdate(
                event.getComponentName(),
                event.getCalledServiceName(),
                event.getCallCount(),
                event.getErrorCount());
    }

    private ComponentState parseState(String remoteState) {
        log.debug("Remote component state === {}", remoteState);
        ComponentState state = ComponentState.UNDEPLOYED;

        switch (remoteState) {
            case "Running": state = ComponentState.STARTED; break;
            case "Pending": state = ComponentState.DEPLOYED; break;
            case "Deleted": state = ComponentState.UNDEPLOYED; break;
        }

        log.debug("Actual component state === {}", state.getName());
        return state;
    }
}
