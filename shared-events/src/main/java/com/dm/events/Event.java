package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
public class Event {

    public enum EVENT_TYPE {
        DEPLOYMENT_STATUS_UPDATE,
        SERVICE_UPDATE,
        NAMESPACE_UPDATE,
        DEPENDENCY_UPDATE
    }

    private EVENT_TYPE type;
    private String componentName;
    private String eventTime;
    private String creationTime;

    Event(EVENT_TYPE type, String componentName, DateTime eventTime, DateTime creationTime) {
        this.setType(type);
        this.setComponentName(componentName);

        if (eventTime != null) {
            this.setEventTime(eventTime.toString());
        } else {
            this.setEventTime(DateTime.now().toString());
        }

        if(creationTime != null){
            this.setCreationTime(creationTime.toString());
        } else {
            this.setCreationTime(null);
        }
    }

    public DateTime getEventDateTime() {
        return new DateTime(this.eventTime);
    }

    public DateTime getCreationDateTime() {
        return new DateTime(this.creationTime);
    }
}
