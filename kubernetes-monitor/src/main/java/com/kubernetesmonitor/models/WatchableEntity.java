package com.kubernetesmonitor.models;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class WatchableEntity {
    private String name;
    private String namespace;
    private String serviceName;
    private DateTime creationTime;

    public WatchableEntity() {}

    WatchableEntity(WatchableEntity entity) {
        this.setName(entity.getName());
        this.setNamespace(entity.getNamespace());
        this.setCreationTime(entity.getCreationTime());
        this.setServiceName(entity.getServiceName());
    }
}
