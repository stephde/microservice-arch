package com.kubernetesconsumer.events;

import com.dm.events.DeploymentEvent;
import com.dm.events.ServiceEvent;
import com.kubernetesconsumer.models.KubeEvent;
import com.kubernetesconsumer.models.Pod;
import com.kubernetesconsumer.models.Service;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Date;

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
                .setServiceName(pod.getServiceName())
                .setRuntimeEnv(pod.getRuntimeEnv());
    }

    public ServiceEvent createServiceEvent(Service service, DateTime eventTime) {
        return new ServiceEvent(
                service.getName(),
                service.getPorts(),
                service.getClusterIP(),
                eventTime,
                service.getCreationTime());
    }
}
