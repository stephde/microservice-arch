package com.modelmaintainer.Exception;

public class ElementNotFoundException extends Exception{
    enum ELEMENT_TYPE { COMPONENT, COMPONENT_TYPE, TENANT}

    private ELEMENT_TYPE type;
    private String elementName;
    private static final String MESSAGE = "Element (%s) of type %s could not be found!";

    public ElementNotFoundException(ELEMENT_TYPE type, String elementName) {
        super(String.format(MESSAGE, elementName, type.toString()));
        this.type = type;
        this.elementName = elementName;
    }
}
