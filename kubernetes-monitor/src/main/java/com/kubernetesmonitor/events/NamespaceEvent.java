package com.kubernetesmonitor.events;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class NamespaceEvent extends Event {

    private String name;
    private String namespace;

    public NamespaceEvent(String name, String namespace, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.NAMESPACE_UPDATE, name, eventTime, creationTime);
        this.setName(name);
        this.setNamespace(namespace);
    }
}
