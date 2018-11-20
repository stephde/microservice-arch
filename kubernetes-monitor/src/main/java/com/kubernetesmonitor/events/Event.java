package com.kubernetesmonitor.events;

import lombok.Data;

@Data
public class Event {

    public enum EVENT_TYPE { DEPLOYMENT_STATUS_UPDATE }

    private final EVENT_TYPE type;
    private final String componentName;
}
