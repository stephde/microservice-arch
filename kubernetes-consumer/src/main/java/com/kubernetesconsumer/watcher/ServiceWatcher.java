package com.kubernetesconsumer.watcher;

import com.dm.events.ServiceEvent;
import com.google.gson.reflect.TypeToken;
import com.kubernetesconsumer.events.EventFactory;
import com.kubernetesconsumer.events.Publisher;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.models.Service;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceWatcher extends AbstractWatcher<V1Service> {

    @Autowired
    Publisher publisher;
    @Autowired
    EventFactory eventFactory;

    public ServiceWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.SERVICE_WATCHER);
    }

    @Override
    void watchCallback(Watch.Response<V1Service> item) {
        Service service = this.responseParser.parseServiceResponse(item.object);
        log.info("ServiceWatcher received: {} : {}", item.type, service);
        ServiceEvent serviceEvent = eventFactory.createServiceEvent(service, DateTime.now());
        publisher.publishEvent(serviceEvent);
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
