package com.dm.events;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

@Data
public class ServiceEvent extends Event {

    List<Integer> ports;

    public ServiceEvent(String componentName, List<Integer> ports, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.SERVICE_UPDATE, componentName, eventTime, creationTime);
        this.setPorts(ports);
    }
}
