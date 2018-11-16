package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.ComponentStatus;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ComponentStatus;
import io.kubernetes.client.util.Watch;

public class ComponentStatusWatcher extends AbstractWatcher<V1ComponentStatus> {

    public ComponentStatusWatcher(KubernetesConnector connector) {
        super(connector);
    }

    @Override
    void watchCallback(Watch.Response<V1ComponentStatus> item) {
        ComponentStatus status = this.responseParser.parseComponentStatusResponse(item.object);
        System.out.printf("%s : %s%n", item.type, status);
    }

    @Override
    Call watchCall() throws ApiException {
        return this.kubernetesConnector.getListComponentStatusCall();
    }

    @Override
    protected Watch<V1ComponentStatus> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1ComponentStatus>>() {
        }.getType());
    }
}
