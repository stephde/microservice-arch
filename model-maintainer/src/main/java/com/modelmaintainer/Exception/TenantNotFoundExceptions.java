package com.modelmaintainer.Exception;

public class TenantNotFoundExceptions extends ElementNotFoundException {

    public TenantNotFoundExceptions(String tenantName) {
        super(ELEMENT_TYPE.TENANT, tenantName);
    }
}
