package com.kubernetesmonitor.events;

import lombok.Data;
import lombok.ToString;
import org.joda.time.DateTime;

@Data
@ToString(callSuper = true)
public class DeploymentEvent extends Event {

    private String status;
    private String nodeName;
    private String serviceName;

    public DeploymentEvent(String componentName, String status, String nodeName, String serviceName, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.DEPLOYMENT_STATUS_UPDATE, componentName, eventTime, creationTime);
        this.setStatus(status);
        this.setNodeName(nodeName);
        this.setServiceName(serviceName);
    }
}
