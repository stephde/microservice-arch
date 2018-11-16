package com.kubernetesmonitor.kubernetes;

import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
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
public class KubernetesConnector {
    private CoreV1Api api;
    private ApiClient client;

    public KubernetesConnector() throws IOException {
        this.client = Config.defaultClient();
        Configuration.setDefaultApiClient(this.client);

        this.api = new CoreV1Api(client);
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
        return this.api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getListServiceCall() throws ApiException {
        return this.api.listServiceForAllNamespacesCall(
                null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }

    public Call getListComponentStatusCall() throws ApiException {
        return this.api.listComponentStatusCall(null, null, null, null, null, null, null, null, Boolean.TRUE, null, null);
    }
}
