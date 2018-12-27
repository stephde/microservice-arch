package com.modelgenerator;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.ComponentType;
import de.mdelab.comparch.MonitoredProperty;
import de.mdelab.comparch.Tenant;
import de.mdelab.comparch.src.de.mdelab.comparch.DefaultComparchFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.Exception;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

@Service
@Slf4j
public class ModelWrapper {

    @Autowired
    private CompArchLoader loader;
    private Architecture model;
    private DefaultComparchFactoryImpl factory;

    private List<Component> removedInstances = Lists.newArrayList();

    private static final String PROPERTY_LASTE_UPDATE = "lastUpdate";
    private static final String PROPERTY_CREATION_TIME = "creationTime";

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

    public JsonObject getModelAsJSON() {

        JsonObject json = new JsonObject();
        json.addProperty("instance", model.toString());

        model.getComponentTypes().forEach(ct -> {
            JsonObject jsonComponent = new JsonObject();
            jsonComponent.addProperty("name", ct.getName());

            JsonArray instances = new JsonArray();
            ct.getInstances().forEach(i -> {
                JsonObject jsonInstance = new JsonObject();
                jsonInstance.addProperty("name", i.getName());
                jsonInstance.addProperty("state", i.getState().getName());
                jsonInstance.addProperty("tenant", i.getTenant().getName());

                // add monitored properties
                i.getMonitoredProperties().forEach(prop -> {
                    jsonInstance.addProperty(prop.getName(), prop.getValue());
                });

                instances.add(jsonInstance);
            });
            jsonComponent.add("Instances", instances);

            jsonComponent.addProperty("Parameters", ct.getParameterTypes().toString());
            json.add(ct.getName(), jsonComponent);
        });

        return json;
    }

    public void handleInstanceStateUpdate(String componentName, String instanceName, String nodeName, ComponentState state, DateTime eventTime, DateTime creationTime) {
//        Component component = this.model.getTenants()
//                .stream()
//                .map(Tenant::getComponents)
//                .flatMap(List::stream)
//                .filter(c -> c.getName().equals(compName))
//                .findFirst()
//                .orElseThrow(() -> new Exception("Component not found in model"));
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
            component.getMonitoredProperties().add(createProperty(PROPERTY_LASTE_UPDATE, eventTime));
            component.getMonitoredProperties().add(createProperty(PROPERTY_CREATION_TIME, creationTime));
            log.info("Changed {} state to: {}", component.getName(), state);
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

    private MonitoredProperty createProperty(String name, DateTime time) {
        MonitoredProperty monitoredProperty = this.factory.createMonitoredProperty();
        monitoredProperty.setName(name);
        monitoredProperty.setValue(time.toString());
        return monitoredProperty;
    }

    private Component getComponent(String componentName, String instanceName) throws Exception {
        return getComponentType(componentName)
                .getInstances()
                .stream()
                .filter(i -> i.getName().equals(instanceName))
                .findFirst()
                .orElseThrow(() -> new Exception("Instance - " + instanceName + " not found"));
    }

    private Component getOrCreateComponent(String componentName, String instanceName) {
        Component component;

        try {
            component = getComponent(componentName, instanceName);
        } catch (Exception e) {
            component = this.factory.createComponent();
            log.info("Settings component name to: {}", instanceName);
            component.setName(instanceName);
            getComponentType(componentName).getInstances().add(component);
            log.info("Created and added component : {}", component);
        }

        return component;
    }

    private Optional<Component> removeComponent(String componentName, String instanceName) {
        Component component;
        try {
            component = getComponent(componentName, instanceName);
        } catch (Exception e) {
            return Optional.empty();
        }

        getComponentType(componentName)
                .getInstances()
                .remove(component);

        return Optional.of(component);
    }

    private ComponentType getComponentType(String typeName) {
        ComponentType componentType;

        try {
            componentType = this.model.getComponentTypes()
                .stream()
                .filter(c -> c.getName().equals(typeName))
                .findFirst()
                .orElseThrow(() -> new Exception("ComponentType - " + typeName + " not found in model"));
        } catch (Exception ex) {
            componentType = createComponentType(typeName);
        }

        return componentType;
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
                    .orElseThrow(() -> new Exception("Tenant - " + nodeName + " not found in model"));
        } catch (Exception e) {
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
}
