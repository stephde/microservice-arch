package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
public class NamespaceEvent extends Event {

    private String name;

    public NamespaceEvent(String name, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.NAMESPACE_UPDATE, name, eventTime, creationTime);
        this.setName(name);
    }
}
