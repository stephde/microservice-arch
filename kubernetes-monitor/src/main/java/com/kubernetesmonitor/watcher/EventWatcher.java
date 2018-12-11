package com.kubernetesmonitor.watcher;

import com.google.gson.reflect.TypeToken;
import com.kubernetesmonitor.events.DeploymentEvent;
import com.kubernetesmonitor.events.Publisher;
import com.kubernetesmonitor.kubernetes.KubernetesConnector;
import com.kubernetesmonitor.models.KubeEvent;
import com.kubernetesmonitor.models.Pod;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Event;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventWatcher extends AbstractWatcher<V1Event> {

    @Autowired
    Publisher publisher;

    public EventWatcher(KubernetesConnector connector) {
        super(connector);
        this.watch();
    }

    @Override
    void watchCallback(Watch.Response<V1Event> item) {
        KubeEvent event = this.responseParser.parseEventResponse(item.object);
        log.info("{}", event.toString());
    }

    @Override
    Call watchCall() throws ApiException {
        log.info("#### Executing event watch call...");
        return this.kubernetesConnector.getEventsCall();
    }

    @Override
    protected Watch<V1Event> initWatch() throws ApiException {
        return Watch.createWatch(this.kubernetesConnector.getClient(), this.watchCall(), new TypeToken<Watch.Response<V1Event>>() {
        }.getType());
    }
}
