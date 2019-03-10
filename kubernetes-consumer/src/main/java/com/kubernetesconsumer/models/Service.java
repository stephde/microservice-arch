package com.kubernetesconsumer.models;

import lombok.Data;

import java.util.List;

@Data
public class Service extends WatchableEntity {
    private String externalName;
    private List<Integer> ports;
    private String clusterIP;

    public Service(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
