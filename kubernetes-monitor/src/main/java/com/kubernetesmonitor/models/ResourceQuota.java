package com.kubernetesmonitor.models;

import io.kubernetes.client.custom.Quantity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResourceQuota extends WatchableEntity {
    private Map<String, Quantity> hard;
    private Map<String, String> hard_status;
    private List<String> scopes;
    private Map<String, String> used;

    public ResourceQuota(WatchableEntity watchableEntity){
        super(watchableEntity);
    }
}
