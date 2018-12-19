package com.kubernetesmonitor.events;

import com.kubernetesmonitor.models.WatchableEntity;
import lombok.Data;
import org.joda.time.DateTime;

@Data
public class NamespaceEvent extends Event {

    private String name;

    public NamespaceEvent(WatchableEntity namespace, DateTime eventTime) {
        super(EVENT_TYPE.NAMESPACE_UPDATE, namespace.getName(), eventTime, namespace.getCreationTime());
        this.setName(namespace.getName());
    }
}
