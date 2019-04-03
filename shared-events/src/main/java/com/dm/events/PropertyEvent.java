package com.dm.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
@ToString(callSuper = true)
public class PropertyEvent extends Event {
    private String propertyName;
    private String value;

    public PropertyEvent(String callingServiceName, DateTime eventTime) {
        super(EVENT_TYPE.PROPERTY_UPDATE, callingServiceName, eventTime, eventTime);
    }
}
