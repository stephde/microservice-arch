package com.kubernetesmonitor.events;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class DeploymentEvent extends Event {

    private String status;
    private String nodeName;
    private String serviceName;
    //ToDo: add pod instance identifier

    public DeploymentEvent(String componentName, String status, String nodeName, String serviceName) {
        super(EVENT_TYPE.DEPLOYMENT_STATUS_UPDATE, componentName);
        this.setStatus(status);
        this.setNodeName(nodeName);
        this.setServiceName(serviceName);
    }
}
