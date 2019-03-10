package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.List;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
public class ServiceEvent extends Event {

    private List<Integer> ports;
    private String clusterIP;

    public ServiceEvent(String componentName, List<Integer> ports, String clusterIP, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.SERVICE_UPDATE, componentName, eventTime, creationTime);
        this.setPorts(ports);
        this.setClusterIP(clusterIP);
    }
}
