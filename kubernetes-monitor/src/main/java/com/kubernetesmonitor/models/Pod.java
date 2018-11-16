package com.kubernetesmonitor.models;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class Pod extends WatchableEntity {
    private DateTime startTime;
    private int containerCount;
    private String serviceName;
    private String nodeName;
    private String status;

    public Pod(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
