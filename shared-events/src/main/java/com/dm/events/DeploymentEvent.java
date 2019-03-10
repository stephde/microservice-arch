package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
public class DeploymentEvent extends Event {

    private String status;
    private String nodeName;
    private String serviceName;
    private String runtimeEnv;

    public DeploymentEvent(String componentName, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.DEPLOYMENT_STATUS_UPDATE, componentName, eventTime, creationTime);
    }
}
