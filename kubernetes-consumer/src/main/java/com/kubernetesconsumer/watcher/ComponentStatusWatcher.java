package com.kubernetesconsumer.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.models.ComponentStatus;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ComponentStatus;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ComponentStatusWatcher extends AbstractWatcher<V1ComponentStatus> {

    /**
     * Compenstatuses refer to internal kubernetes services (e.g. scheduler, controller-manager, etcd)
     * @param connector
     */

    public ComponentStatusWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.OTHER);
    }

    @Override
    void watchCallback(Watch.Response<V1ComponentStatus> item) {
        ComponentStatus status = this.responseParser.parseComponentStatusResponse(item.object);
        log.info("{} : {}", item.type, status);
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
