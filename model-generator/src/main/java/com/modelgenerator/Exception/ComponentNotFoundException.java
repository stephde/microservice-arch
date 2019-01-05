package com.modelgenerator.Exception;

public class ComponentNotFoundException extends ElementNotFoundException {

    public ComponentNotFoundException(String componentName) {
        super(ELEMENT_TYPE.COMPONENT, componentName);
    }
}
