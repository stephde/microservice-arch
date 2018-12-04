package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.events.ServiceEvent;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.Service;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ServiceWatcher extends AbstractWatcher<V1Service> {

    @Autowired
    Publisher publisher;

    public ServiceWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1Service> item) {
        Service service = this.responseParser.parseServiceResponse(item.object);
        log.info("ServiceWatcher received: {} : {}", item.type, service);
        publisher.publishEvent(new ServiceEvent(service.getName(), service.getPorts()));
    }

    @Override
    Call watchCall() throws ApiException {
        return this.kubernetesConnector.getListServiceCall();
    }

    @Override
    protected Watch<V1Service> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Service>>() {
        }.getType());
    }
}
