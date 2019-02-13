package com.modelmaintainer;

import com.google.common.collect.Lists;
import com.modelmaintainer.Exception.ComponentNotFoundException;
import com.modelmaintainer.Exception.ComponentTypeNotFoundException;
import com.modelmaintainer.Exception.ElementNotFoundException;
import com.modelmaintainer.Exception.TenantNotFoundExceptions;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.ComponentType;
import de.mdelab.comparch.Connector;
import de.mdelab.comparch.MonitoredProperty;
import de.mdelab.comparch.ProvidedInterface;
import de.mdelab.comparch.RequiredInterface;
import de.mdelab.comparch.Tenant;
import de.mdelab.comparch.src.de.mdelab.comparch.DefaultComparchFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

@org.springframework.stereotype.Component
@Slf4j
public class ModelWrapper {

    @Autowired
    private CompArchLoader loader;
    private Architecture model;
    private DefaultComparchFactoryImpl factory;

    private List<Component> removedInstances = Lists.newArrayList();

    private static final String PROPERTY_LAST_UPDATE = "lastUpdate";
    private static final String PROPERTY_CREATION_TIME = "creationTime";
    private static final String PROPERTY_INVOCATION_COUNT = "invocationCount";
    private static final String PROPERTY_ERROR_COUNT = "errorCount";

    public ModelWrapper(@NotNull CompArchLoader loader) {
        this.loader = loader;
        this.factory = new DefaultComparchFactoryImpl();
        try {
//            this.model = loader.loadModel();
            this.generateModel("default");
            log.info(this.model.toString());
        } catch (RuntimeException e) {
            log.error("Could not load model due to: {}", e.getMessage());
        }
    }

    public void save() {
        this.loader.saveModel(this.model);
    }

    public Architecture getModel() {
        return this.model;
    }

    public void generateModel(String name) {

        this.model = this.factory.createArchitecture();
        this.model.setName(name);
    }

    public void handleInstanceStateUpdate(String componentName, String instanceName, String nodeName, ComponentState state, DateTime eventTime, DateTime creationTime) {
        if ( isStaleInstance(instanceName) ) {
            // do nothing if update is irrelevant
            return;
        }

        if (state.equals(ComponentState.UNDEPLOYED)) {
            log.info("Removing instance: {}", instanceName);
            Optional<Component> removedIntance = removeComponent(componentName, instanceName);
            removedIntance.ifPresent(i -> this.removedInstances.add(i));
        } else {
            Tenant tenant = getTenant(nodeName);

            Component component = getOrCreateComponent(componentName, instanceName);
            component.setState(state);
            component.setTenant(tenant);
            component.getMonitoredProperties().add(createProperty(PROPERTY_LAST_UPDATE, eventTime));
            component.getMonitoredProperties().add(createProperty(PROPERTY_CREATION_TIME, creationTime));
            log.info("Changed {} state to: {}", component.getName(), state);
        }
    }

    public void handleDependencyUpdate(String callingService, String calledService, Integer callCount, Integer errorCount) {
        try {
            Component callingComponent = this.getAnyComponent(callingService);
            Component calledComponent = this.getAnyComponent(calledService);

            Optional<Connector> connector = getConnector(callingComponent, calledComponent);
            if(connector.isPresent()) {
                updateConnectorProperties(connector.get(), callCount, errorCount);
            } else {
                //create connector
                Connector newConnector = this.createConnection(callingComponent, calledComponent);
                newConnector.getMonitoredProperties().add(createProperty(PROPERTY_INVOCATION_COUNT, callCount));
                newConnector.getMonitoredProperties().add(createProperty(PROPERTY_ERROR_COUNT, errorCount));
            }
        } catch (ElementNotFoundException e) {
            log.error(e.getMessage());
        }
    }

    Optional<Connector> getConnector(Component origin, Component target) {
        return origin.getRequiredInterfaces()
                .stream()
                .filter(i -> i.getConnector().getTarget().getComponent().equals(target))
                .map(RequiredInterface::getConnector)
                .findFirst();
    }

    void updateConnectorProperties(Connector connector, Integer callCount, Integer errorCount) {
        Optional<MonitoredProperty> optionalInvocationCountProp = connector.getMonitoredProperties().stream().filter(p -> p.getName().equals(PROPERTY_INVOCATION_COUNT)).findFirst();
        Optional<MonitoredProperty> optionalErrorCountProp = connector.getMonitoredProperties().stream().filter(p -> p.getName().equals(PROPERTY_ERROR_COUNT)).findFirst();

        if (optionalInvocationCountProp.isPresent()) {
            String callCountValue = callCount != null ? callCount.toString() : "";
            optionalInvocationCountProp.get().setValue(callCountValue);
        } else {
            MonitoredProperty property = createProperty(PROPERTY_INVOCATION_COUNT, callCount);
            connector.getMonitoredProperties().add(property);
        }

        if (optionalErrorCountProp.isPresent()) {
            String errorCountValue = errorCount != null ? errorCount.toString() : "";
            optionalErrorCountProp.get().setValue(errorCountValue);
        } else {
            MonitoredProperty property = createProperty(PROPERTY_ERROR_COUNT, errorCount);
            connector.getMonitoredProperties().add(property);
        }
    }

    /**
     * return true if the update relates to an already removed instance
     * @param instanceName
     * @return
     */
    public boolean isStaleInstance(String instanceName) {
        return removedInstances
                .stream()
                .anyMatch(c -> c.getName().equals(instanceName));
    }

    private MonitoredProperty createProperty(String name, Object value) {
        Optional<Object> optionalValue = Optional.ofNullable(value);
        MonitoredProperty monitoredProperty = this.factory.createMonitoredProperty();
        monitoredProperty.setName(name);
        monitoredProperty.setValue(optionalValue.orElse("NONE").toString());
        return monitoredProperty;
    }

    private Component getComponent(String componentName, String instanceName) throws ElementNotFoundException {
        return getComponentType(componentName)
                .getInstances()
                .stream()
                .filter(i -> i.getName().equals(instanceName))
                .findFirst()
                .orElseThrow(() -> new ComponentNotFoundException(instanceName));
    }

    private Component getAnyComponent(String componentName) throws ElementNotFoundException {
        return getComponentType(componentName)
                .getInstances()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ComponentNotFoundException(componentName));
    }

    private Component getOrCreateComponent(String serviceName, String instanceName) {
        Component component;

        try {
            component = getComponent(serviceName, instanceName);
        } catch (ElementNotFoundException e) {
            component = this.factory.createComponent();
            log.info("Settings component name to: {}", instanceName);
            component.setName(instanceName);

            ComponentType componentType;
            try {
                componentType = getComponentType(serviceName);
            } catch (ComponentTypeNotFoundException ex) {
                componentType = createComponentType(serviceName);
            }
            componentType.getInstances().add(component);
            log.info("Created and added component : {}", component);
        }

        return component;
    }

    private Optional<Component> removeComponent(String componentName, String instanceName) {
        Component component;
        try {
            component = getComponent(componentName, instanceName);
            getComponentType(componentName)
                .getInstances()
                .remove(component);
        } catch (ElementNotFoundException e) {
            return Optional.empty();
        }

        return Optional.of(component);
    }

    private ComponentType getComponentType(String typeName) throws ComponentTypeNotFoundException {
        return this.model.getComponentTypes()
                .stream()
                .filter(c -> c.getName().equals(typeName))
                .findFirst()
                .orElseThrow(() -> new ComponentTypeNotFoundException(typeName));
    }

    private ComponentType createComponentType(String componentName) {
        ComponentType ct = this.factory.createComponentType();
        ct.setName(componentName);
        this.model.getComponentTypes().add(ct);

        return ct;
    }

    private Tenant getTenant(String nodeName) {
        Tenant tenant;

        try {
            tenant = this.model.getTenants()
                    .stream()
                    .filter(t -> t.getName().equals(nodeName))
                    .findFirst()
                    .orElseThrow(() -> new TenantNotFoundExceptions(nodeName));
        } catch (TenantNotFoundExceptions e) {
            tenant = createTenant(nodeName);
        }

        return tenant;
    }


    private Tenant createTenant(String name) {
        Tenant tenant = this.factory.createTenant();
        tenant.setName(name);
        log.info("Created Tenant {} : {}", name, tenant);

        this.model.getTenants().add(tenant);
        return tenant;
    }

    private Connector createConnection(Component callingComponent, Component calledComponent) {
        RequiredInterface requiredInterface = this.factory.createRequiredInterface();
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
}
