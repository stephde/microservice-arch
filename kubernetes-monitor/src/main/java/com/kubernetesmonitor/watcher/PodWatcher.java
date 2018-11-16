package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Pod;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;

public class PodWatcher extends AbstractWatcher<V1Pod> {

    public PodWatcher(KubernetesConnector connector) {
        super(connector);
    }

    @Override
    void watchCallback(Watch.Response<V1Pod> item) {
        Pod pod = this.responseParser.parsePodResponse(item.object);
        System.out.printf("Event type: %s : %s %n", item.type, pod.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        System.out.println("#### Executing podwatch call...");
        return this.kubernetesConnector.getListPodCall();
    }

    @Override
    protected Watch<V1Pod> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Pod>>() {
        }.getType());
    }
}
