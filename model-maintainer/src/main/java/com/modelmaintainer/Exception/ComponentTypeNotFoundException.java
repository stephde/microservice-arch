package com.modelmaintainer.Exception;

public class ComponentTypeNotFoundException extends ElementNotFoundException {

    public ComponentTypeNotFoundException(String componenTypeName) {
        super(ELEMENT_TYPE.COMPONENT_TYPE, componenTypeName);
    }
}
