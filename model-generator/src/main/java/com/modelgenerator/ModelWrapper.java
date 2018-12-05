package com.modelgenerator;

import com.kubernetesmonitor.events.Event;
import com.google.gson.JsonObject;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.Tenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
public class ModelWrapper {

    @Autowired
    private CompArchLoader loader;
    private Architecture model;

    public ModelWrapper(@NotNull CompArchLoader loader) {
        this.loader = loader;
        try {
            this.model = loader.loadModel();
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

    public JsonObject getModelAsJSON() {

        JsonObject json = new JsonObject();
        json.addProperty("instance", model.toString());

        model.getComponentTypes().forEach(c -> {
            JsonObject jsonComponent = new JsonObject();

            jsonComponent.addProperty("name", c.getName());
            jsonComponent.addProperty("Instances", c.getInstances().toString());
            jsonComponent.addProperty("Parameters", c.getParameterTypes().toString());
            json.add(c.getName(), jsonComponent);
        });

        return json;
    }

    public void handleStateUpdate(String componentName, String instanceName, ComponentState value) throws Exception {
//        Component component = this.model.getTenants()
//                .stream()
//                .map(Tenant::getComponents)
//                .flatMap(List::stream)
//                .filter(c -> c.getName().equals(compName))
//                .findFirst()
//                .orElseThrow(() -> new Exception("Component not found in model"));

        Component component = this.model.getComponentTypes()
                .stream()
                .filter(c -> c.getName().equals(componentName))
                .findFirst()
                .orElseThrow(() -> new Exception("ComponentType - " + componentName + " not found in model"))
                .getInstances()
                .stream()
                .filter(i -> i.getName().equals(instanceName))
                .findFirst()
                .orElseThrow(() -> new Exception("Instance - " + instanceName + " not found"));

        component.setState(value);
        log.info("Changed {} state to: {}", component.getName(), value);
    }
}
