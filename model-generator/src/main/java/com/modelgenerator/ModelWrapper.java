package com.modelgenerator;

import com.google.gson.JsonArray;
import com.kubernetesmonitor.events.Event;
import com.google.gson.JsonObject;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.ComponentType;
import de.mdelab.comparch.Tenant;
import de.mdelab.comparch.src.de.mdelab.comparch.DefaultComparchFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.EList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.Exception;
import java.util.Iterator;

import javax.validation.constraints.NotNull;

@Service
@Slf4j
public class ModelWrapper {

    @Autowired
    private CompArchLoader loader;
    private Architecture model;
    private DefaultComparchFactoryImpl factory;

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

        model.getComponentTypes().forEach(c -> {
            JsonObject jsonComponent = new JsonObject();
            jsonComponent.addProperty("name", c.getName());

            JsonArray instances = new JsonArray();
            c.getInstances().forEach(i -> {
                JsonObject jsonInstance = new JsonObject();
                jsonInstance.addProperty("name", i.getName());
                jsonInstance.addProperty("state", i.getState().getName());
                jsonInstance.addProperty("tenant", i.getTenant().getName());
                instances.add(jsonInstance);
            });
            jsonComponent.add("Instances", instances);

            jsonComponent.addProperty("Parameters", c.getParameterTypes().toString());
            json.add(c.getName(), jsonComponent);
        });

        return json;
    }

    public void handleInstanceStateUpdate(String componentName, String instanceName, String nodeName, ComponentState state) {
//        Component component = this.model.getTenants()
//                .stream()
//                .map(Tenant::getComponents)
//                .flatMap(List::stream)
//                .filter(c -> c.getName().equals(compName))
//                .findFirst()
//                .orElseThrow(() -> new Exception("Component not found in model"));

        if(state.equals(ComponentState.UNDEPLOYED)) {
            log.info("Removing instance: {}", instanceName);
            removeComponent(componentName, instanceName);
        } else {
            Tenant tenant = getTenant(nodeName);

            try {
                Component component = getComponent(componentName, instanceName);
                component.setState(state);
                component.setTenant(tenant);
                log.info("Changed {} state to: {}", component.getName(), state);
            } catch (Exception e) {
                // ToDo: define custom exception
                // component does not exist yet
                createAndAddComponent(componentName, instanceName, tenant, state);
            }
        }

    }

    private Component getComponent(String componentName, String instanceName) throws Exception {
        return getComponentType(componentName)
                .getInstances()
                .stream()
                .filter(i -> i.getName().equals(instanceName))
                .findFirst()
                .orElseThrow(() -> new Exception("Instance - " + instanceName + " not found"));
    }

    private void createAndAddComponent(String componentName, String instanceName, Tenant tenant, ComponentState state) {
        Component component = this.factory.createComponent();
        component.setName(instanceName);
        component.setState(state);
        component.setTenant(tenant);

        getComponentType(componentName).getInstances().add(component);
        log.info("Created and added component {} : {}", componentName, component);
    }

    private void removeComponent(String componentName, String instanceName) {
        getComponentType(componentName)
                .getInstances()
                .removeIf(component -> component.getName().equals(instanceName));
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
