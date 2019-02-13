package com.kubernetesconsumer.watcher;

import com.dm.events.DeploymentEvent;
import com.google.gson.reflect.TypeToken;
import com.kubernetesconsumer.events.EventFactory;
import com.kubernetesconsumer.events.Publisher;
import com.kubernetesconsumer.kubernetes.KubernetesConnector;
import com.kubernetesconsumer.models.KubeEvent;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Event;
import io.kubernetes.client.util.Watch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventWatcher extends AbstractWatcher<V1Event> {


    @Autowired
    Publisher publisher;
    @Autowired
    EventFactory eventFactory;

    public EventWatcher(KubernetesConnector connector) {
        super(connector, WATCHER_TYPE.EVENT_WATCHER);
    }

    @Override
    void watchCallback(Watch.Response<V1Event> item) {
        KubeEvent event = this.responseParser.parseEventResponse(item.object);
        log.info("{}", event.toString());

        handleDeleteEvents(event);
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

    private void handleDeleteEvents(KubeEvent event) {
        if(event.getReason().equals("Killing")) {
            DeploymentEvent deletionEvent = eventFactory.createPodDeletionEvent(event);
            publisher.publishEvent(deletionEvent);
        }
    }
}
