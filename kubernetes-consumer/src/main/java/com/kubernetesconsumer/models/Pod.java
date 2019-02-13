package com.kubernetesconsumer.models;

import lombok.Data;
import lombok.ToString;
import org.joda.time.DateTime;

@Data
@ToString(callSuper = true)
public class Pod extends WatchableEntity {
    private DateTime startTime;
    private int containerCount;
    private String nodeName;
    private String status;

    // ToDo: add containers

    public Pod(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
