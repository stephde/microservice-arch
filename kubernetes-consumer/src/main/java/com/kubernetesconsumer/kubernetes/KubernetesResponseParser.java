package com.kubernetesconsumer.kubernetes;

import com.kubernetesconsumer.models.*;
import io.kubernetes.client.models.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
public class KubernetesResponseParser {

    public ComponentStatus parseComponentStatusResponse(V1ComponentStatus status) {
        return new ComponentStatus(parseMetadata(status.getMetadata()))
                .setConditions(status.getConditions());
    }

    public Pod parsePodResponse(V1Pod podResponse) {
        Optional<V1Container> optionalContainer = podResponse.getSpec().getContainers().stream().findFirst();

        Pod pod = new Pod(parseMetadata(podResponse.getMetadata()))
                .setStartTime(podResponse.getStatus().getStartTime())
                .setStatus(podResponse.getStatus().getPhase())
                .setContainerCount(podResponse.getSpec().getContainers().size())
                .setNodeName(podResponse.getSpec().getNodeName());

        Map<String, String> labels = podResponse.getMetadata().getLabels();

        if(labels != null) {
            List<String> labelStrings = labels.keySet().stream().map(key -> {
                log.info("Label - {} : {} ", key, labels.get(key));
                return String.format("%s : %s", key, labels.get(key));
            }).collect(Collectors.toList());
        }

        optionalContainer.ifPresent(c -> {
            List<String> envs = new ArrayList<>();
            if (c.getEnv() != null) {
                    envs = c.getEnv()
                            .stream()
                            .map(e -> String.format("%s : %s", e.getName(), e.getValue()))
                            .collect(Collectors.toList());
            }

            String runtimeEnv = extractRuntimeEnv(c.getCommand(), envs, c.getArgs());
            if(runtimeEnv != null) {
                pod.setRuntimeEnv(runtimeEnv);
            }
        });

        return pod;
    }

    public Service parseServiceResponse(V1Service serviceResponse) {
        List<Integer> ports = serviceResponse.getSpec()
                .getPorts()
                .stream()
                .map(V1ServicePort::getPort)
                .collect(Collectors.toList());

        return new Service(parseMetadata(serviceResponse.getMetadata()))
                .setExternalName(serviceResponse.getSpec().getExternalName())
                .setPorts(ports)
                .setClusterIP(serviceResponse.getSpec().getClusterIP());
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
//                .setMatchLabels(replicaSet.getSpec().getSelector().getMatchLabels())
                .setMinReadySeconds(replicaSet.getSpec().getMinReadySeconds());
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
                .setEventTime(event.getEventTime())
                .setMessage(event.getMessage())
                .setName(event.getInvolvedObject().getName())
                .setReason(event.getReason())
                .setRelated(event.getRelated() != null ? event.getRelated().getName() : "")
                .setSource(event.getSource().getComponent())
                .setType(event.getType());
    }

    public WatchableEntity parseMetadata(V1ObjectMeta metadata) {
        return new WatchableEntity()
                .setName(metadata.getName())
                .setNamespace(metadata.getNamespace())
                .setCreationTime(metadata.getCreationTimestamp())
                .setServiceName(extractServiceName(metadata.getName()));
    }

    private String extractRuntimeEnv(List<String> cmd, List<String> envs, List<String> args) {
        log.info("Command: {}", cmd);
        log.info("Env Vars: {}", envs);
        log.info("Arguments: {}", args);

        return null;
    }

    private String extractServiceName(String instanceName) {
        int cutIndex = instanceName.indexOf("-");

        if ( cutIndex > 0) {
            return instanceName.substring(0, cutIndex);
        }

        return instanceName;
    }
}
