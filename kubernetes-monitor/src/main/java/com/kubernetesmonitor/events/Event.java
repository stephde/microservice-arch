package com.kubernetesmonitor.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    public enum EVENT_TYPE { DEPLOYMENT_STATUS_UPDATE, SERVICE_UPDATE, NAMESPACE_UPDATE }

    private EVENT_TYPE type;
    private String componentName;
}
