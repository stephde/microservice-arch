package com.kubernetesmonitor.events;

import lombok.Data;
//import org.joda.time.DateTime;

@Data
public class NamespaceEvent extends Event {

    private String name;
    private String namespace;
//    private DateTime creationTime;

    public NamespaceEvent(String name, String namespace/*, DateTime creationTime*/) {
        super(EVENT_TYPE.NAMESPACE_UPDATE, name);
        this.setName(name);
        this.setNamespace(namespace);
//        this.setCreationTime(creationTime);
    }
}
