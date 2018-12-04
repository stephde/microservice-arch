package com.kubernetesmonitor.models;

import lombok.Data;

import java.util.Map;

@Data
public class ReplicaSet extends WatchableEntity {
    private Integer replicas;
    private Map<String, String> matchLabels;
    private Integer minReadySeconds;

    public ReplicaSet(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
