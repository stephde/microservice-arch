package com.kubernetesmonitor.kubernetes;

import com.kubernetesmonitor.models.ComponentStatus;
import com.kubernetesmonitor.models.Pod;
import com.kubernetesmonitor.models.Service;
import com.kubernetesmonitor.models.WatchableEntity;
import io.kubernetes.client.models.*;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class KubernetesResponseParser {

    public ComponentStatus parseComponentStatusResponse(V1ComponentStatus status) {
        return new ComponentStatus(parseMetadata(status.getMetadata()))
                .setConditions(status.getConditions());
    }

    public Pod parsePodResponse(V1Pod podResponse) {
        return new Pod(parseMetadata(podResponse.getMetadata()))
                .setStartTime(podResponse.getStatus().getStartTime())
                .setStatus(podResponse.getStatus().getPhase())
                .setContainerCount(podResponse.getSpec().getContainers().size())
                .setNodeName(podResponse.getSpec().getNodeName())
                .setServiceName(podResponse.getSpec().getServiceAccountName());
    }

    public Service parseServiceResponse(V1Service serviceResponse) {
        List<Integer> ports = serviceResponse.getSpec()
                .getPorts()
                .stream()
                .map(V1ServicePort::getPort)
                .collect(Collectors.toList());

        return new Service(parseMetadata(serviceResponse.getMetadata()))
                .setExternalName(serviceResponse.getSpec().getExternalName())
                .setPorts(ports);
    }

    private WatchableEntity parseMetadata(V1ObjectMeta metadata) {
        return new WatchableEntity()
                .setName(metadata.getName())
                .setNamespace(metadata.getNamespace())
                .setCreationTime(metadata.getCreationTimestamp());
    }
}
