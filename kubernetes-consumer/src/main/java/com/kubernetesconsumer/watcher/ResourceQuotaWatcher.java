package com.kubernetesconsumer.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesconsumer.events.Publisher;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ResourceQuota;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceQuotaWatcher extends AbstractWatcher<V1ResourceQuota> {

    @Autowired
    Publisher publisher;

    public ResourceQuotaWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.OTHER);
    }

    @Override
    void watchCallback(Watch.Response<V1ResourceQuota> item) {
        log.info("#### V1ResourceQuota Watcher ---- Event type: {} : {}", item.type, item.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing V1ResourceQuota call...");
        return this.kubernetesConnector.getResourceQuotaCall();
    }

    @Override
    protected Watch<V1ResourceQuota> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1ResourceQuota>>() {
        }.getType());
    }
}
