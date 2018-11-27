package com.modelgenerator;

import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.Component;
import de.mdelab.comparch.ComponentState;
import de.mdelab.comparch.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class ModelWrapper {

    @Autowired private CompArchLoader loader;
    private Architecture model;

    public ModelWrapper(@NotNull CompArchLoader loader) {
        this.loader = loader;
        try {
            this.model = loader.loadModel();
            System.out.println(this.model.toString());
        } catch (RuntimeException e) {
            System.out.println("Could not load model due to: " + e.getMessage());
        }
    }

    public void save() {
        this.loader.saveModel(this.model);
    }

    public void update(Object updateEvent) throws Exception {
        handleStateUpdate(updateEvent);
    }

    public Architecture getModel() {
        return this.model;
    }

    private void handleStateUpdate(Object stateUpdateEvent) throws Exception {
        String compName = "Category Item Filter";
        ComponentState state = ComponentState.DEPLOYED;

        Component component = this.model.getTenants()
                .stream()
                .map(Tenant::getComponents)
                .flatMap(List::stream)
                .filter(c -> c.getName().equals(compName))
                .findFirst()
                .orElseThrow(() -> new Exception("Component not found in model"));

        //component.setState(state);
        System.out.println("Changed component state to: " + state.toString()); //component.toString());
    }
}
