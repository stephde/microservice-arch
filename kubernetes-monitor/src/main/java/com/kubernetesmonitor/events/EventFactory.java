package com.kubernetesmonitor.events;

import com.dm.events.DeploymentEvent;
import com.kubernetesmonitor.models.KubeEvent;
import com.kubernetesmonitor.models.Pod;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class EventFactory {
    private static final String STATE_DELETED = "Deleted";

    public DeploymentEvent createPodDeletionEvent(KubeEvent kubeEvent) {
        return new DeploymentEvent(kubeEvent.getName(), kubeEvent.getEventTime(), kubeEvent.getCreationTime())
                .setStatus(STATE_DELETED)
                .setServiceName(kubeEvent.getServiceName());

    }

    public DeploymentEvent createPodUpdateEvent(Pod pod, DateTime eventTime) {
        return new DeploymentEvent(pod.getName(), eventTime, pod.getCreationTime())
                .setStatus(pod.getStatus())
                .setNodeName(pod.getNodeName())
                .setServiceName(pod.getServiceName());
    }
}
