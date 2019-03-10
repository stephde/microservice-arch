package com.modelmaintainer.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.RequiredInterface;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JSONParser {

    public JsonObject getModelAsJSON(Architecture model) {

        JsonObject json = new JsonObject();
        json.addProperty("instance", model.toString());

        model.getComponentTypes().forEach(ct -> {
            JsonObject jsonComponent = new JsonObject();
            jsonComponent.addProperty("name", ct.getName());

            JsonArray instances = new JsonArray();
            ct.getInstances().forEach(i -> instances.add(getComponentAsJson(i)));
            jsonComponent.add("instances", instances);

            // add component type properties
            ct.getMonitoredProperties().forEach(prop -> jsonComponent.addProperty(prop.getName(), prop.getValue()));

            jsonComponent.addProperty("parameters", ct.getParameterTypes().toString());
            json.add(ct.getName(), jsonComponent);
        });

        return json;
    }

    private JsonObject getComponentAsJson(de.mdelab.comparch.Component component) {
        JsonObject jsonInstance = new JsonObject();
        jsonInstance.addProperty("name", component.getName());
        jsonInstance.addProperty("state", component.getState().getName());
        jsonInstance.addProperty("tenant", component.getTenant().getName());

        // add monitored properties
        component.getMonitoredProperties().forEach(prop -> jsonInstance.addProperty(prop.getName(), prop.getValue()));


        JsonArray requiredInterfaces = getInterfacesAsJSON(component.getRequiredInterfaces());
        jsonInstance.add("requiredInterfaces", requiredInterfaces);

        return jsonInstance;
    }

    private JsonArray getInterfacesAsJSON(List<RequiredInterface> interfaces) {
        JsonArray jsonArray = new JsonArray();
        interfaces.forEach(i -> {
            String targetComponentName = i.getConnector().getTarget().getComponent().getName();

            JsonObject jsonComponent = new JsonObject();
            jsonComponent.addProperty("targetName", targetComponentName);
            i.getConnector().getMonitoredProperties().forEach(prop -> {
                jsonComponent.addProperty(prop.getName(), prop.getValue());
            });
            jsonArray.add(jsonComponent);
        });
        return jsonArray;
    }
}
