package com.modelmaintainer.model;

import com.google.common.collect.Lists;
import com.modelmaintainer.Exception.ComponentNotFoundException;
import com.modelmaintainer.Exception.ComponentTypeNotFoundException;
import com.modelmaintainer.Exception.ElementNotFoundException;
import com.modelmaintainer.Exception.TenantNotFoundExceptions;
import de.mdelab.comparch.ArchitecturalElement;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.ComponentType;
import de.mdelab.comparch.Connector;
import de.mdelab.comparch.MonitoredProperty;
import de.mdelab.comparch.RequiredInterface;
import de.mdelab.comparch.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.EList;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import static com.modelmaintainer.model.MonitorableProperties.PROPERTY_INVOCATION_COUNT;

@Service
@Slf4j
public class ModelWrapper {

    @Autowired
    private CompArchLoader loader;
    @Autowired
    private CompArchFactory factory;
    private Architecture model;

    private List<de.mdelab.comparch.Component> removedInstances = Lists.newArrayList();

    public ModelWrapper(@NotNull CompArchLoader loader) {
        this.loader = loader;
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
        this.model = this.factory.createModel(name);
    }

    public void handleInstanceStateUpdate(String componentName, String instanceName, String nodeName, ComponentState state, DateTime eventTime, DateTime creationTime, String runtimeEnv) {
        if ( isStaleInstance(instanceName) ) {
            // do nothing if update is irrelevant
            return;
        }

        if (state.equals(ComponentState.UNDEPLOYED)) {
            log.info("Removing instance: {}", instanceName);
            Optional<Component> removedIntance = removeComponent(componentName, instanceName);
            removedIntance.ifPresent(i -> this.removedInstances.add(i));
        } else {
            // ToDo: if we want to support multiple tenants we need to save a list with the names
            Tenant tenant = getTenant(model.getName());

            Component component = getOrCreateComponent(componentName, instanceName);
            component.setState(state);
            component.setTenant(tenant);
            EList<MonitoredProperty> properties = component.getMonitoredProperties();
            properties.add(this.factory.createNodeProperty(nodeName));
            properties.add(this.factory.createLastUpdateProperty(eventTime));
            properties.add(this.factory.createCreationTimeProperty(creationTime));
            properties.add(this.factory.createRuntimeEnvProperty(runtimeEnv));
            log.info("Changed {} state to: {}", component.getName(), state);
        }
    }

    public void handleServiceStateUpdate(String componentTypeName, String clusterIP, List<Integer> ports) {
        try {
            ComponentType componentType = getComponentType(componentTypeName);
            EList<MonitoredProperty> properties = componentType.getMonitoredProperties();
            properties.add(this.factory.createClusterIPProperty(clusterIP));
            properties.add(this.factory.createPortsProperty(ports));
        } catch (ComponentTypeNotFoundException e) {
            log.warn(e.getMessage());
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
                Connector newConnector = this.factory.createConnector(callingComponent, calledComponent);
                EList<MonitoredProperty> properties = newConnector.getMonitoredProperties();
                properties.add(this.factory.createInvocationProperty(callCount));
                properties.add(this.factory.createErrorCountProperty(errorCount));
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
        Optional<MonitoredProperty> optionalInvocationCountProp = getProperty(connector, PROPERTY_INVOCATION_COUNT);
        Optional<MonitoredProperty> optionalErrorCountProp = getProperty(connector, PROPERTY_INVOCATION_COUNT);

        if (optionalInvocationCountProp.isPresent()) {
            String callCountValue = callCount != null ? callCount.toString() : "";
            optionalInvocationCountProp.get().setValue(callCountValue);
        } else {
            MonitoredProperty property = this.factory.createInvocationProperty(callCount);
            connector.getMonitoredProperties().add(property);
        }

        if (optionalErrorCountProp.isPresent()) {
            String errorCountValue = errorCount != null ? errorCount.toString() : "";
            optionalErrorCountProp.get().setValue(errorCountValue);
        } else {
            MonitoredProperty property = this.factory.createErrorCountProperty(errorCount);
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
                .anyMatch(c -> c.getName().equalsIgnoreCase(instanceName));
    }


    private Component getComponent(String componentName, String instanceName) throws ElementNotFoundException {
        return getComponentType(componentName)
                .getInstances()
                .stream()
                .filter(i -> i.getName().equalsIgnoreCase(instanceName))
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
            component = this.factory.createComponent(instanceName);

            ComponentType componentType;
            try {
                componentType = getComponentType(serviceName);
            } catch (ComponentTypeNotFoundException ex) {
                componentType = this.factory.createComponentType(serviceName);
                this.model.getComponentTypes().add(componentType);
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
        if(typeName == null) {
            throw new ComponentTypeNotFoundException(typeName);
        }
        return this.model.getComponentTypes()
                .stream()
                .filter(c -> c.getName().equalsIgnoreCase(typeName))
                .findFirst()
                .orElseThrow(() -> new ComponentTypeNotFoundException(typeName));
    }

    private Tenant getTenant(String tenantName) {
        Tenant tenant;

        try {
            tenant = this.model.getTenants()
                    .stream()
                    .filter(t -> t.getName().equalsIgnoreCase(tenantName))
                    .findFirst()
                    .orElseThrow(() -> new TenantNotFoundExceptions(tenantName));
        } catch (TenantNotFoundExceptions e) {
            tenant = this.factory.createTenant(tenantName);
            this.model.getTenants().add(tenant);
        }

        return tenant;
    }

    public Optional<MonitoredProperty> getProperty(ArchitecturalElement element, String type) {
        return element.getMonitoredProperties()
                .stream()
                .filter(p -> p.getName().equals(type))
                .findFirst();
    }

}
