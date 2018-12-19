package com.kubernetesmonitor.models;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString(callSuper = true)
public class ReplicaSet extends WatchableEntity {
    private Integer replicas;
//    private Map<String, String> matchLabels;
    private Integer minReadySeconds;
    private String status;

    //ToDo: we can add a list of containers from podSpec (e.g. Ports, env vars, image, ...)

    public ReplicaSet(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
