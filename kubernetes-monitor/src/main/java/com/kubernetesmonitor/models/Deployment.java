package com.kubernetesmonitor.models;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class Deployment extends WatchableEntity {
    private Integer replicas;
    private Integer unavailableReplicas;
    private Integer availableReplicas;
    private Integer collisionCount;

    public Deployment(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
