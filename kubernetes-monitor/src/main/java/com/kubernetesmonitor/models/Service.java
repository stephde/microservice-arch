package com.kubernetesmonitor.models;

import lombok.Data;

import java.util.List;

@Data
public class Service extends WatchableEntity {
    private String externalName;
    private List<Integer> ports;

    public Service(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
