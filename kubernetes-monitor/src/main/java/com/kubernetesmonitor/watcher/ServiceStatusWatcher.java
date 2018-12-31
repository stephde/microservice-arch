package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Deployment;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1ServiceStatus;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceStatusWatcher extends AbstractWatcher<V1ServiceStatus> {

    @Autowired
    Publisher publisher;

    public ServiceStatusWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.OTHER);
    }

    @Override
    void watchCallback(Watch.Response<V1ServiceStatus> item) {
        log.info("#### V1ServiceStatus Watcher ---- Event type: {} : {}", item.type, item.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing V1ServiceStatus call...");
        return this.kubernetesConnector.getDeploymentCall();
    }

    @Override
    protected Watch<V1ServiceStatus> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1ServiceStatus>>() {
        }.getType());
    }
}
