package com.kubernetesmonitor.events;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DeploymentEvent extends com.kubernetesmonitor.events.Event {

    private String status;

    public DeploymentEvent(String componentName, String status) {
        super(EVENT_TYPE.DEPLOYMENT_STATUS_UPDATE, componentName);
        this.setStatus(status);
    }
}
