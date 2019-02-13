package com.kubernetesconsumer.kubernetes;

import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class acts as a connector to the Kubernetes api.
 * The API documentation can be found under https://github.com/kubernetes-client/java
 * When initialized it automatically tries to connect to the default api which is one of:
 *
 * <ul>
 *   <li>If $KUBECONFIG is defined, use that config file.
 *   <li>If $HOME/.kube/config can be found, use that.
 *   <li>If the in-cluster service account can be found, assume in cluster config.
 *   <li>Default to localhost:8080 as a last resort.
 * </ul>
 */

@Service
@Slf4j
public class KubernetesConnector {
    private CoreV1Api api;
    private AppsV1Api appsApi;
    private ApiClient client;
    private final KubernetesConfig config;

    public KubernetesConnector(KubernetesConfig config) throws IOException {
        this.config = config;
        this.client = Config.defaultClient();
        Configuration.setDefaultApiClient(this.client);

        log.info("Connecting to kubernetes on: {}", this.client.getBasePath());
        log.info("Using namespace: {}", this.getNamespace());

        this.api = new CoreV1Api(client);
        this.appsApi = new AppsV1Api(client);
    }

    public List<String> getAllPods() throws ApiException {
        return api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(pod -> pod.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public List<String> getAllComponentStati() throws ApiException {
        return api.listComponentStatus(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(comp -> comp.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public List<String> getAllServices() throws ApiException {
        return api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(comp -> comp.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public List<String> getAllEvents() throws ApiException {
        return api.listEventForAllNamespaces(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(comp -> comp.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public List<String> getAllResorceQuotas() throws ApiException {
        return api.listResourceQuotaForAllNamespaces(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(comp -> comp.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public List<String> getAllNodes() throws ApiException {
        return api.listNode(null, null, null, null, null, null, null, null, null)
                .getItems()
                .stream()
                .map(comp -> comp.getMetadata().getName())
                .collect(Collectors.toList());
    }

    public void setInfiniteWatchTimout() {
        this.client.getHttpClient().setReadTimeout(0, TimeUnit.MILLISECONDS);
    }

    public ApiClient getClient() {
        return this.client;
    }

    public Call getListPodCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.api.listNamespacedPodCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getListServiceCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.api.listNamespacedServiceCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.api.listServiceForAllNamespacesCall(
                null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getListComponentStatusCall() throws ApiException {

        return this.api.listComponentStatusCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getServiceStatusCall() throws ApiException {
        return this.api.readNamespacedServiceStatusCall(null, this.getNamespace(), null, null, null);
    }

    public Call getResourceQuotaCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.api.listNamespacedResourceQuotaCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.api.listResourceQuotaForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getEventsCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.api.listNamespacedEventCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.api.listEventForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getNamespacesCall() throws ApiException {
        return this.api.listNamespaceCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getReplicaSetCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.appsApi.listNamespacedReplicaSetCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.appsApi.listReplicaSetForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getDeploymentCall() throws ApiException {

        if(this.hasValidNamespace()) {
            return this.appsApi.listNamespacedDeploymentCall(this.getNamespace(), null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
        }

        return this.appsApi.listDeploymentForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public String getNamespace() {
        return this.config.NAMESPACE;
    }

    public void setNamespace(String namespace) {
        log.info("Setting kubernetes namespace to: {}", namespace);
        this.config.NAMESPACE = namespace;
    }

    public boolean hasValidNamespace() {
        return this.getNamespace() != null && ! this.getNamespace().isEmpty();
    }
}
