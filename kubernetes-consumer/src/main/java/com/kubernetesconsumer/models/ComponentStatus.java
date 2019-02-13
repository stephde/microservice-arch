package com.kubernetesconsumer.models;

import io.kubernetes.client.models.V1ComponentCondition;
import lombok.Data;

import java.util.List;

@Data
public class ComponentStatus extends WatchableEntity {

    List<V1ComponentCondition> conditions;

    public ComponentStatus(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
