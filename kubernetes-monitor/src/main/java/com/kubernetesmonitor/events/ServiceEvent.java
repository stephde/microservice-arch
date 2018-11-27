package com.kubernetesmonitor.events;

import lombok.Data;
import java.util.List;

@Data
public class ServiceEvent extends com.kubernetesmonitor.events.Event {

    List<Integer> ports;

    public ServiceEvent(String componentName, List<Integer> ports) {
        super(EVENT_TYPE.SERVICE_UPDATE, componentName);
        this.setPorts(ports);
    }
}
