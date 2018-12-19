package com.kubernetesmonitor.models;

import lombok.Data;
import lombok.ToString;
import org.joda.time.DateTime;

@Data
@ToString(callSuper = true)
public class KubeEvent extends WatchableEntity {
//    private String action; // usually null
    private DateTime lastTimestamp;
    private DateTime eventTime;
//    private String kind; // is always Event
    private String message;
    private String reason; // Scheduled, Pulling, ...
    private String related;
    private String type; // Normal, Warning, Error?
//    private String reportingComponent;
//    private String reportingInstance;
    private String source;
    private String name;


    public KubeEvent(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
