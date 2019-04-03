package com.modelmaintainer.model;

import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentType;
import de.mdelab.comparch.Connector;
import de.mdelab.comparch.MonitoredProperty;
import de.mdelab.comparch.ProvidedInterface;
import de.mdelab.comparch.Tenant;
import de.mdelab.comparch.src.de.mdelab.comparch.DefaultComparchFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.modelmaintainer.model.MonitorableProperties.*;

@Service
@Slf4j
public class CompArchFactory {
    private DefaultComparchFactoryImpl factory;

    public CompArchFactory() {
        this.factory = new DefaultComparchFactoryImpl();
    }

    public Architecture createModel(String name) {
        Architecture model = this.factory.createArchitecture();
        model.setName(name);
        return model;
    }

    public MonitoredProperty createNodeProperty(String nodeName) {
        return createProperty(PROPERTY_NODE, nodeName);
    }

    public MonitoredProperty createLastUpdateProperty(DateTime time) {
        return createProperty(PROPERTY_LAST_UPDATE, time.toString());
    }

    public MonitoredProperty createCreationTimeProperty(DateTime time) {
        return createProperty(PROPERTY_CREATION_TIME, time.toString());
    }

    public MonitoredProperty createInvocationProperty(Integer invocationCount) {
        return createProperty(PROPERTY_INVOCATION_COUNT, invocationCount == null ? "NONE" : String.valueOf(invocationCount));
    }

    public MonitoredProperty createErrorCountProperty(Integer errorCount) {
        return createProperty(PROPERTY_ERROR_COUNT, errorCount == null ? "NONE" : String.valueOf(errorCount));
    }

    public MonitoredProperty createRuntimeEnvProperty(String runtime) {
        return createProperty(PROPERTY_RUNTIME_ENV, runtime == null ? "NONE" : String.valueOf(runtime));
    }

    public MonitoredProperty createClusterIPProperty(String ip) {
        return createProperty(PROPERTY_CLUSTER_IP, ip == null ? "NONE" : String.valueOf(ip));
    }

    public MonitoredProperty createPortsProperty(List<Integer> ports) {
        String portsString = ports == null
                ? "NONE"
                : ports.toString();

        return createProperty(PROPERTY_PORTS, portsString);
    }

    public MonitoredProperty createHttpExceptionProperty(String value) {
        return createProperty(PROPERTY_HTTP_EXCEPTION_COUNT, value);
    }

    private MonitoredProperty createProperty(String name, String value) {
        MonitoredProperty monitoredProperty = this.factory.createMonitoredProperty();
        monitoredProperty.setName(name);
        monitoredProperty.setValue(value);
        return monitoredProperty;
    }

    public Component createComponent(String name) {
        Component component = this.factory.createComponent();
        log.info("Settings component name to: {}", name);
        component.setName(name.toLowerCase());
        return component;
    }

    public ComponentType createComponentType(String componentName) {
        ComponentType ct = this.factory.createComponentType();
        ct.setName(componentName.toLowerCase());
        return ct;
    }

    public Connector createConnector(de.mdelab.comparch.Component callingComponent, de.mdelab.comparch.Component calledComponent) {
        de.mdelab.comparch.RequiredInterface requiredInterface = this.factory.createRequiredInterface();
        requiredInterface.setComponent(callingComponent);

        ProvidedInterface providedInterface = this.factory.createProvidedInterface();
        providedInterface.setComponent(calledComponent);

        Connector connector = this.factory.createConnector();
        connector.setName(callingComponent.getName() + "-connector");
        connector.setSource(requiredInterface);
        connector.setTarget(providedInterface);

        requiredInterface.setConnector(connector);
        providedInterface.getConnectors().add(connector);

        return connector;
    }

    public Tenant createTenant(String name) {
        Tenant tenant = this.factory.createTenant();
        tenant.setName(name);
        log.info("Created Tenant {} : {}", name, tenant);
        return tenant;
    }
}
