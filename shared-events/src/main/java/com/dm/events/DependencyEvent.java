package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
public class DependencyEvent extends Event {
    private String calledServiceName;
    private Integer callCount;
    private Integer errorCount;

    public DependencyEvent(String callingServiceName, DateTime eventTime, DateTime creationTime) {
        super(EVENT_TYPE.DEPENDENCY_UPDATE, callingServiceName, eventTime, creationTime);
    }
}
