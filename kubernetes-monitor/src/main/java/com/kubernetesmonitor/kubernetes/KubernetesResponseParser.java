package com.kubernetesmonitor.kubernetes;

import com.kubernetesmonitor.models.*;
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

    public Deployment parseDeploymentResponse(V1Deployment deploymentResponse) {
        return new Deployment(parseMetadata(deploymentResponse.getMetadata()))
                .setReplicas(deploymentResponse.getSpec().getReplicas())
                .setAvailableReplicas(deploymentResponse.getStatus().getAvailableReplicas())
                .setUnavailableReplicas(deploymentResponse.getStatus().getUnavailableReplicas())
                .setCollisionCount(deploymentResponse.getStatus().getCollisionCount());
    }

    public ReplicaSet parseReplicaSetResponse(V1ReplicaSet replicaSet) {
        return new ReplicaSet(parseMetadata(replicaSet.getMetadata()))
                .setReplicas(replicaSet.getSpec().getReplicas())
                .setMinReadySeconds(replicaSet.getSpec().getMinReadySeconds())
//                .setMatchLabels(replicaSet.getSpec().getSelector().getMatchLabels())
                .setServiceName(replicaSet.getSpec().getTemplate().getSpec().getServiceAccountName());
    }

    public ResourceQuota parseResourceQuota(V1ResourceQuota resourceQuota) {
        return new ResourceQuota(parseMetadata(resourceQuota.getMetadata()))
                .setHard(resourceQuota.getSpec().getHard())
                .setHard_status(resourceQuota.getStatus().getHard())
                .setUsed(resourceQuota.getStatus().getUsed())
                .setScopes(resourceQuota.getSpec().getScopes());
    }

    public KubeEvent parseEventResponse(V1Event event) {
        return new KubeEvent(parseMetadata(event.getMetadata()))
                .setLastTimestamp(event.getLastTimestamp())
                .setMessage(event.getMessage())
                .setName(event.getInvolvedObject().getName())
                .setReason(event.getReason())
                .setRelated(event.getRelated() != null ? event.getRelated().getName() : "")
                .setSource(event.getSource().getComponent())
                .setType(event.getType());
    }

    private WatchableEntity parseMetadata(V1ObjectMeta metadata) {
        return new WatchableEntity()
                .setName(metadata.getName())
                .setNamespace(metadata.getNamespace())
                .setCreationTime(metadata.getCreationTimestamp());
    }
}
